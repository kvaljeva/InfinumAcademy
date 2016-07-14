package valjevac.kresimir.homework3.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.PokemonDetailsActivity;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.adapters.PokemonAdapter;
import valjevac.kresimir.homework3.listeners.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.PokemonModel;

public class PokemonListActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 420;
    public static final String POKEMON = "Pokemon";
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

        pokemonList = new ArrayList<>();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (data.getExtras() != null) {
                PokemonModel pokemon = (PokemonModel) data.getExtras().getSerializable(POKEMON);

                pokemonList.add(pokemon);

                llEmptyStateContainer.setVisibility(View.GONE);
                rvPokemonList.setVisibility(View.VISIBLE);

                pokemonAdapter.update();
            }
        }
    }
}
