package valjevac.kresimir.homework3.activities;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
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

    @BindView(R.id.iv_pokemon_image)
    ImageView ivImage;

    @BindView(R.id.tb_pokemon_details)
    Toolbar toolbar;

    @BindView(R.id.abl_header_pokemon_details)
    AppBarLayout ablPokemonDetails;

    @BindView(R.id.ctl_header_pokemon_details)
    CollapsingToolbarLayout ctlHeaderPokemonDetails;

    private String transformHeightString(String height) {
        height = height.replace(".", "´ ");
        height += "˝";

        return height;
    }

    private void setToolbarTitle() {
        if (ctlHeaderPokemonDetails != null) {
            ctlHeaderPokemonDetails.setTitle(this.getTitle());
            ctlHeaderPokemonDetails.setExpandedTitleColor(ContextCompat.getColor(this,
                    android.R.color.transparent));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);

        ButterKnife.bind(this);

        setToolbarTitle();

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        PokemonModel pokemon = getIntent().getParcelableExtra(PokemonListActivity.POKEMON);

        String height = transformHeightString(String.valueOf(pokemon.getHeight()));
        String weight = String.valueOf(pokemon.getWeight()) + " lbs";

        tvName.setText(pokemon.getName());
        tvDescription.setText(pokemon.getDescription());
        tvHeight.setText(height);
        tvWeight.setText(weight);
        tvCategory.setText(pokemon.getCategory());
        tvAbilities.setText(pokemon.getAbilites());
        ivImage.setImageBitmap(BitmapHelper.loadBitmap(this, pokemon.getImage(), false));
        tvGender.setText(pokemon.getGender());
    }

    @OnClick(R.id.tb_pokemon_details)
    public void setNavigationClickListener(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return false;
    }
}
