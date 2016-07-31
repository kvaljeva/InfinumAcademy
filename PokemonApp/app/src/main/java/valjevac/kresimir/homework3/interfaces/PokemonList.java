package valjevac.kresimir.homework3.interfaces;

import java.util.List;

import valjevac.kresimir.homework3.models.Pokemon;

public interface PokemonList {

    List<Pokemon> getPokemons();

    void addPokemon(Pokemon pokemon);

    void updatePokemon(Pokemon pokemon);

    void removePokemon(Pokemon pokemon);
}
