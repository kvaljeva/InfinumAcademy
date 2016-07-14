package valjevac.kresimir.homework3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.activities.PokemonListActivity;
import valjevac.kresimir.homework3.models.PokemonModel;

public class PokemonDetailsActivity extends AppCompatActivity {
    @BindView(R.id.tv_details_pokemon_name)
    TextView tvPokemonName;

    @BindView(R.id.tv_details_pokemon_desc)
    TextView tvPokemonDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);

        ButterKnife.bind(this);

        PokemonModel pokemon = (PokemonModel) getIntent().getSerializableExtra(PokemonListActivity.POKEMON);

        tvPokemonName.setText(pokemon.getName());
        tvPokemonDescription.setText(pokemon.getDescription());
    }
}
