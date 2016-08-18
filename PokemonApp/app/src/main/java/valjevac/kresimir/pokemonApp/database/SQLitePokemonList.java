package valjevac.kresimir.pokemonApp.database;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import valjevac.kresimir.pokemonApp.interfaces.PokemonList;
import valjevac.kresimir.pokemonApp.models.Pokemon;

public class SQLitePokemonList implements PokemonList {

    @Override
    public List<Pokemon> getPokemons() {
        return SQLite.select().from(Pokemon.class).queryList();
    }

    @Override
    public void addPokemon(Pokemon pokemon) {
        pokemon.save();
    }

    @Override
    public void updatePokemon(Pokemon pokemon) {
        pokemon.update();
    }

    @Override
    public void removePokemon(Pokemon pokemon) {
        pokemon.delete();
    }
}
