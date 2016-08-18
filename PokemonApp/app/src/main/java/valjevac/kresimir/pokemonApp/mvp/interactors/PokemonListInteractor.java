package valjevac.kresimir.pokemonApp.mvp.interactors;

import valjevac.kresimir.pokemonApp.interfaces.DeletePokemonListener;
import valjevac.kresimir.pokemonApp.interfaces.LogoutListener;
import valjevac.kresimir.pokemonApp.interfaces.PokemonListLoadListener;

public interface PokemonListInteractor {

    void getPokemonList(PokemonListLoadListener listener);

    void deletePokemon(int pokemonId, DeletePokemonListener listener);

    void logout(LogoutListener listener);

    void cancel();
}
