package valjevac.kresimir.homework3;

import java.util.ArrayList;

import valjevac.kresimir.homework3.fragments.PokemonListFragment;
import valjevac.kresimir.homework3.interfaces.PokemonList;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.Pokemon;

public class ProcessPokemonList implements Runnable {

    ArrayList<Pokemon> pokemons;
    PokemonList pokemonListDatabase;
    BaseResponse<ArrayList<Data<Pokemon>>> body;

    public OnProcessingFinishedListener listener;

    public interface OnProcessingFinishedListener {

        void onProcessingFinished(ArrayList<Pokemon> pokemons);
    }

    public ProcessPokemonList(BaseResponse<ArrayList<Data<Pokemon>>> body, PokemonList pokemonListDatabase, PokemonListFragment context) {

        this.pokemons = new ArrayList<>();

        this.body = body;
        this.pokemonListDatabase = pokemonListDatabase;

        listener = context;
    }

    @Override
    public void run() {

        for (Data data : body.getData()) {
            if (data.getAttributes() instanceof Pokemon) {
                Pokemon pokemon = (Pokemon) data.getAttributes();

                pokemon.setId(data.getId());

                pokemons.add(pokemon);
                pokemonListDatabase.addPokemon(pokemon);
            }
        }

        listener.onProcessingFinished(pokemons);
    }
}
