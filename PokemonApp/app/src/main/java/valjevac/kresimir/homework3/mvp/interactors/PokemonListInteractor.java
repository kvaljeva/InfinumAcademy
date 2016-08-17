package valjevac.kresimir.homework3.mvp.interactors;

import valjevac.kresimir.homework3.interfaces.DeletePokemonListener;
import valjevac.kresimir.homework3.interfaces.LogoutListener;
import valjevac.kresimir.homework3.interfaces.PokemonListLoadListener;

public interface PokemonListInteractor {

    void getPokemonList(PokemonListLoadListener listener);

    void deletePokemon(int pokemonId, DeletePokemonListener listener);

    void logout(LogoutListener listener);

    void cancel();
}
