package com.ifi.controller;

import com.ifi.bo.PokemonType;
import com.ifi.repository.PokemonTypeRepository;

import java.util.Map;

@Controller
public class PokemonTypeController {
    private PokemonTypeRepository repository = new PokemonTypeRepository();

    @RequestMapping(uri="/pokemon")
    public PokemonType getPokemon(Map<String,String[]> parameters) throws IllegalArgumentException {

        if (parameters == null || parameters.isEmpty()) {
            throw new IllegalArgumentException("parameters should not be empty");
        }

        String[] idParam = parameters.get("id");
        String[] nameParam = parameters.get("name");

        PokemonType pokemon = null;

        if (idParam != null) {
            pokemon = repository.findPokemonById(Integer.parseInt(idParam[0]));
        } else if (nameParam != null) {
            pokemon = repository.findPokemonByName((nameParam[0]));
        } else {
            throw new IllegalArgumentException("unknown parameter");
        }

        return pokemon;
    }


}