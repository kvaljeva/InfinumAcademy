package valjevac.kresimir.homework3.mvp.interactors;

import okhttp3.RequestBody;
import valjevac.kresimir.homework3.interfaces.AddPokemonListener;
import valjevac.kresimir.homework3.models.Pokemon;

public interface AddPokemonInteractor {

    void addPokemon(Pokemon pokemon, int[] types, int[] moves, RequestBody body, AddPokemonListener listener);

    void cancel();
}
