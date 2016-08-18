package valjevac.kresimir.pokemonApp.interfaces;

import java.util.List;

import valjevac.kresimir.pokemonApp.models.Pokemon;

public interface PokemonList {

    List<Pokemon> getPokemons();

    void addPokemon(Pokemon pokemon);

    void updatePokemon(Pokemon pokemon);

    void removePokemon(Pokemon pokemon);
}
