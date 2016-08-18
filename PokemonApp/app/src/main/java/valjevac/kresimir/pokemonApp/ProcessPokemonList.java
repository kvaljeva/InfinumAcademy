package valjevac.kresimir.pokemonApp;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import valjevac.kresimir.pokemonApp.fragments.PokemonListFragment;
import valjevac.kresimir.pokemonApp.interfaces.PokemonList;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.ExtendedData;
import valjevac.kresimir.pokemonApp.models.Move;
import valjevac.kresimir.pokemonApp.models.Pokemon;
import valjevac.kresimir.pokemonApp.models.PokemonType;

public class ProcessPokemonList implements Runnable {

    private ArrayList<Pokemon> pokemons;

    private PokemonList pokemonListDatabase;

    private BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>> body;

    private Handler handler;

    public OnProcessingFinishedListener listener;

    private boolean isCanceled = false;

    public interface OnProcessingFinishedListener {

        void onProcessingFinished(ArrayList<Pokemon> pokemons);
    }

    public ProcessPokemonList(BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>> body, PokemonList pokemonListDatabase, PokemonListFragment context) {

        this.pokemons = new ArrayList<>();

        this.body = body;
        this.pokemonListDatabase = pokemonListDatabase;

        listener = context;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {

        for (ExtendedData data : body.getData()) {
            if (data.getAttributes() instanceof Pokemon) {

                Pokemon pokemon = (Pokemon) data.getAttributes();
                pokemon.setId(data.getId());

                @SuppressWarnings("unchecked")
                ArrayList<Move> movesList = (ArrayList<Move>) data.getRelationships().getMoves().getData();
                String moves = processInnerListItems(movesList);

                @SuppressWarnings("unchecked")
                ArrayList<PokemonType> typesList = (ArrayList<PokemonType>) data.getRelationships().getModel().getData();
                String types = processInnerListItems(typesList);

                pokemon.setMoves(moves);
                pokemon.setTypes(types);

                pokemons.add(pokemon);
                pokemonListDatabase.addPokemon(pokemon);
            }
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isCanceled) {
                    listener.onProcessingFinished(pokemons);
                }
            }
        });
    }

    private <T> String processInnerListItems(ArrayList<T> list) {
        String items = "";

        for (Object object : list) {
            if (object instanceof Move) {
                items += ((Move) object).getName().substring(0, 1).toUpperCase() + ((Move) object).getName().substring(1) + "\n";
            }
            else if (object instanceof PokemonType) {
                items += ((PokemonType) object).getName().substring(0, 1).toUpperCase() + ((PokemonType) object).getName().substring(1) + "\n";
            }
        }

        return items;
    }

    public void cancel() {
        this.isCanceled = true;
    }
}
