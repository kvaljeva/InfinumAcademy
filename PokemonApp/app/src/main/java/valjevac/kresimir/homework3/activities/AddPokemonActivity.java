package valjevac.kresimir.homework3.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import valjevac.kresimir.homework3.ConfirmationDialog;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.models.PokemonModel;

public class AddPokemonActivity extends AppCompatActivity implements ConfirmationDialog.OnCompleteListener {
    private boolean changesMade = false;

    @BindView(R.id.et_pokemon_name)
    EditText etPokemonName;

    @BindView(R.id.et_pokemon_desc)
    EditText etPokemonDescription;

    @BindView(R.id.et_pokemon_height)
    EditText etPokemonHeight;

    @BindView(R.id.et_pokemon_weight)
    EditText etPokemonWeight;

    @BindView(R.id.et_pokemon_category)
    EditText etPokemonCategory;

    @BindView(R.id.et_pokemon_abilities)
    EditText etPokemonAbilities;

    @BindView(R.id.rl_body)
    RelativeLayout rlActivityBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pokemon);

        ButterKnife.bind(this);
    }

    private boolean checkEmtpyFields() {
        int editTextCount = rlActivityBody.getChildCount();

        for (int i = 0; i < editTextCount; i++) {
            View view = rlActivityBody.getChildAt(i);

            if (view instanceof EditText) {
                EditText editText = (EditText)view;

                if (TextUtils.isEmpty(editText.getText().toString())) {
                    return true;
                }
            }
        }

        return false;
    }

    @OnClick(R.id.btn_save_pokemon)
    public void savePokemon() {
        if (checkEmtpyFields()) {
            Toast toast = Toast.makeText(this, "Please fill out the given fields", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            String pokemonName = etPokemonName.getText().toString();
            String pokemonDesc = etPokemonDescription.getText().toString();
            float pokemonHeight = Float.valueOf(etPokemonHeight.getText().toString());
            float pokemonWeight = Float.valueOf(etPokemonWeight.getText().toString());
            String category = etPokemonCategory.getText().toString();
            String abilities = etPokemonAbilities.getText().toString();

            PokemonModel pokemon = new PokemonModel(pokemonName, pokemonDesc, pokemonHeight,
                    pokemonWeight, category, abilities);

            Intent intent = new Intent();
            intent.putExtra(PokemonListActivity.POKEMON, pokemon);

            setResult(PokemonListActivity.REQUEST_CODE, intent);

            finish();
        }
    }

    @OnTextChanged(R.id.et_pokemon_desc)
    public void notifyDescChange(CharSequence charSequence) {
        changesMade = !TextUtils.isEmpty(charSequence);
    }

    @OnTextChanged(R.id.et_pokemon_name)
    public void notifyNameChange(CharSequence charSequence) {
        changesMade = !TextUtils.isEmpty(charSequence);
    }

    private void showDialog() {
        DialogFragment dialog = new ConfirmationDialog();
        Bundle args = new Bundle();

        args.putString(ConfirmationDialog.TITLE, "Warning");
        args.putString(ConfirmationDialog.MESSAGE, "Are you sure that you want to abandon your data?");

        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "tag");
    }

    @Override
    public void onBackPressed() {
        if (changesMade) {
            showDialog();
        }
        else {
            super.onBackPressed();
        }
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

    public void onComplete(boolean confirmExit) {
        if (confirmExit) {
            finish();
        }
    }
}
