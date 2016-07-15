package valjevac.kresimir.homework3.activities;

import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.StringBuilderPrinter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.PokemonListActivity;
import valjevac.kresimir.homework3.models.PokemonModel;

public class PokemonDetailsActivity extends AppCompatActivity {
    @BindView(R.id.tv_details_pokemon_name)
    TextView tvName;

    @BindView(R.id.tv_details_pokemon_desc)
    TextView tvDescription;

    @BindView(R.id.tv_pokemon_height_value)
    TextView tvHeight;

    @BindView(R.id.tv_pokemon_weight_value)
    TextView tvWeight;

    @BindView(R.id.tv_pokemon_category_value)
    TextView tvCategory;

    @BindView(R.id.tv_pokemon_abilities_value)
    TextView tvAbilities;

    @BindView(R.id.tv_pokemon_gender_value)
    TextView tvGender;

    private String transformHeightString(String height) {
        height = height.replace(".", "´ ");
        height += "˝";

        return height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);

        ButterKnife.bind(this);

        PokemonModel pokemon = getIntent().getParcelableExtra(PokemonListActivity.POKEMON);

        String height = transformHeightString(String.valueOf(pokemon.getHeight()));
        String weight = String.valueOf(pokemon.getWeight()) + " lbs";

        tvName.setText(pokemon.getName());
        tvDescription.setText(pokemon.getDescription());
        tvHeight.setText(height);
        tvWeight.setText(weight);
        tvCategory.setText(pokemon.getCategory());
        tvAbilities.setText(pokemon.getAbilites());
    }
}
