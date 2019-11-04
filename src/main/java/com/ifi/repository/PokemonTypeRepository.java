package com.ifi.repository;

import com.ifi.bo.PokemonType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PokemonTypeRepository {
    private List<PokemonType> pokemons;

    public PokemonTypeRepository() {
        try {
            var pokemonsStream = this.getClass().getResourceAsStream("/pokemons.json");

            var objectMapper = new ObjectMapper();
            var pokemonsArray = objectMapper.readValue(pokemonsStream, PokemonType[].class);
            this.pokemons = Arrays.asList(pokemonsArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PokemonType findPokemonById(int id) {
        System.out.println("Loading Pokemon information for Pokemon id " + id);

        int i = 0;
        boolean found = false;
        PokemonType pokemonType = null;

        while (! found && i < this.pokemons.size()) {
            pokemonType = this.pokemons.get(i);

            if (pokemonType.getId() == id) {
                found = true;
            }
            i++;
        }

        return pokemonType;
    }

    public PokemonType findPokemonByName(String name) {
        System.out.println("Loading Pokemon information for Pokemon name " + name);
        int i = 0;
        boolean found = false;
        PokemonType pokemonType = null;

        while (! found && i < this.pokemons.size()) {
            pokemonType = this.pokemons.get(i);

            if (name.equals(pokemonType.getName())) {
                found = true;
            }
            i++;
        }

        return pokemonType;
    }

    public List<PokemonType> findAllPokemon() {
        return this.pokemons;
    }
}
