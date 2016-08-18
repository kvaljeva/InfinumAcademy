package valjevac.kresimir.pokemonApp.mvp.interactors;

import okhttp3.RequestBody;
import valjevac.kresimir.pokemonApp.interfaces.AddPokemonListener;
import valjevac.kresimir.pokemonApp.models.Pokemon;

public interface AddPokemonInteractor {

    void addPokemon(Pokemon pokemon, int[] types, int[] moves, RequestBody body, AddPokemonListener listener);

    void cancel();
}
