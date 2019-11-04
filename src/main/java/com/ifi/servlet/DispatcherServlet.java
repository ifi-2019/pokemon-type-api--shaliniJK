package com.ifi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifi.controller.Controller;
import com.ifi.controller.HelloController;
import com.ifi.controller.PokemonTypeController;
import com.ifi.controller.RequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    private Map<String, Method> uriMappings = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("Getting request for " + req.getRequestURI());
        var uri = req.getRequestURI();

        if (! uriMappings.containsKey(uri)) {
            resp.sendError(404, "no mapping found for request uri " + uri);
            return;
        }

        var method = getMappingForUri(uri);
        try {
            var instance = method.getDeclaringClass().newInstance();
            var params = req.getParameterMap();

            Object result;
            if (method.getParameterCount() >  0) {
                result = method.invoke(instance, params);
            } else {
                result = method.invoke(instance);
            }
            var objectMapper = new ObjectMapper();
            var json = objectMapper.writeValueAsString(result);
            resp.getWriter().print(json);

        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
            resp.sendError(500, "exception when calling method " + method.getName() + " : " + e.getCause().getMessage());
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // on enregistre notre controller au démarrage de la servlet
        this.registerController(HelloController.class);
        this.registerController(PokemonTypeController.class);

    }

    protected void registerController(Class controllerClass) throws IllegalArgumentException {
        System.out.println("Analysing class " + controllerClass.getName());

        if (controllerClass.getDeclaredAnnotation(Controller.class) == null) {
            throw new IllegalArgumentException(controllerClass.getName() + " is not a controller class");
        }

        Method[] methods = controllerClass.getDeclaredMethods();

        for (int i = 0; i < methods.length; i++) {
            Annotation annotation = methods[i].getAnnotation(RequestMapping.class);
            Class returnType = methods[i].getReturnType();

            if ((annotation != null) && (returnType != void.class)){
                registerMethod(methods[i]);
            }
        }

    }

    protected void registerMethod(Method method) {
        System.out.println("Registering method " + method.getName());
        var requestMapping = method.getAnnotation(RequestMapping.class);
        uriMappings.put(requestMapping.uri(), method);
    }

    protected Map<String, Method> getMappings(){
        return this.uriMappings;
    }

    protected Method getMappingForUri(String uri){
        return this.uriMappings.get(uri);
    }
}