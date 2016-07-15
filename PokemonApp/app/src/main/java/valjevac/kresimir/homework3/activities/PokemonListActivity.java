package valjevac.kresimir.homework3.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.adapters.PokemonAdapter;
import valjevac.kresimir.homework3.listeners.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.PokemonModel;

public class PokemonListActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 420;
    public static final String POKEMON = "Pokemon";
    public static final String POKEMON_LIST_SATE = "Pokemon List State";
    public static final String EMPTY_STATE = "Empty State";
    private ArrayList<PokemonModel> pokemonList;
    private PokemonAdapter pokemonAdapter;

    @BindView(R.id.recycler_view_pokemon_list)
    RecyclerView rvPokemonList;

    @BindView(R.id.ll_empty_state_container)
    LinearLayout llEmptyStateContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        ButterKnife.bind(this);
        boolean isEmptyState = true;

        if (savedInstanceState == null) {
            pokemonList = new ArrayList<>();

            PokemonModel model = new PokemonModel("Bulbasaur", "Bulbasaur can be seen napping in the bright sunlight. There is a seed on its back. By soaking up the sun's rays, the seed grows progressively larger.",
                    2.04, 15.2, "Seed", "Overgrow");

            pokemonList.add(model);
        }
        else {
            pokemonList = savedInstanceState.getParcelableArrayList(POKEMON_LIST_SATE);
            isEmptyState = savedInstanceState.getBoolean(EMPTY_STATE);
        }

        pokemonAdapter = new PokemonAdapter(this, pokemonList, new RecyclerViewClickListener<PokemonModel>() {
            @Override
            public void OnClick(PokemonModel object) {
                Intent intent = new Intent(PokemonListActivity.this, PokemonDetailsActivity.class);
                intent.putExtra(POKEMON, object);

                startActivity(intent);
            }
        });

        rvPokemonList.setAdapter(pokemonAdapter);
        rvPokemonList.setLayoutManager(new LinearLayoutManager(this));

        updatePokemonListOverview(isEmptyState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item_add, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(PokemonListActivity.this, AddPokemonActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updatePokemonListOverview(boolean isEmptyState) {
        if (isEmptyState) {
            llEmptyStateContainer.setVisibility(View.VISIBLE);
            rvPokemonList.setVisibility(View.GONE);
        }
        else {
            llEmptyStateContainer.setVisibility(View.GONE);
            rvPokemonList.setVisibility(View.VISIBLE);

            pokemonAdapter.update(pokemonList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                PokemonModel pokemon = data.getExtras().getParcelable(POKEMON);

                pokemonList.add(pokemon);

                updatePokemonListOverview(false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(POKEMON_LIST_SATE, pokemonList);

        if (pokemonList != null && pokemonList.size() > 0) {
            outState.putBoolean(EMPTY_STATE, false);
        }
        else {
            outState.putBoolean(EMPTY_STATE, true);
        }
    }
}
