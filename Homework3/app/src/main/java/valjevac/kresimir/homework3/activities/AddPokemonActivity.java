package valjevac.kresimir.homework3.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.models.PokemonModel;

public class AddPokemonActivity extends AppCompatActivity {
    private boolean changesMade = false;

    @BindView(R.id.et_pokemon_name)
    EditText etPokemonName;

    @BindView(R.id.et_pokemon_desc)
    EditText etPokemonDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pokemon);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_save_pokemon)
    public void savePokemon() {
        String pokemonName = etPokemonName.getText().toString();
        String pokemonDesc = etPokemonDescription.getText().toString();

        PokemonModel pokemon = new PokemonModel(pokemonName, pokemonDesc);

        Intent intent = new Intent();
        intent.putExtra(PokemonListActivity.POKEMON, pokemon);

        setResult(PokemonListActivity.REQUEST_CODE, intent);

        finish();
    }

    @OnTextChanged(R.id.et_pokemon_desc)
    public void notifyDescChange() {
        this.changesMade = true;
    }

    @OnTextChanged(R.id.et_pokemon_name)
    public void notifyNameChange() {
        this.changesMade = true;
    }

    @Override
    public void onBackPressed() {
        if (changesMade) {
            // show dialog
        }
    }
}
