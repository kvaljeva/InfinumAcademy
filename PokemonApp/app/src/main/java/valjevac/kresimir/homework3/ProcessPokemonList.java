package valjevac.kresimir.homework3;

import android.os.Handler;
import android.os.Looper;

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

    Handler handler;

    public OnProcessingFinishedListener listener;

    public boolean isCanceled = false;

    public interface OnProcessingFinishedListener {

        void onProcessingFinished(ArrayList<Pokemon> pokemons);
    }

    public ProcessPokemonList(BaseResponse<ArrayList<Data<Pokemon>>> body, PokemonList pokemonListDatabase, PokemonListFragment context) {

        this.pokemons = new ArrayList<>();

        this.body = body;
        this.pokemonListDatabase = pokemonListDatabase;

        listener = context;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {

        for (Data data : body.getData()) {
            if (data.getAttributes() instanceof Pokemon) {

                if (isCanceled) {
                    break;
                }

                Pokemon pokemon = (Pokemon) data.getAttributes();
                pokemon.setId(data.getId());

                pokemons.add(pokemon);
                pokemonListDatabase.addPokemon(pokemon);
            }
        }

        if (isCanceled) {
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onProcessingFinished(pokemons);
            }
        });
    }

    public void cancel() {
        this.isCanceled = true;
    }
}
