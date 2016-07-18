package valjevac.kresimir.homework3.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import valjevac.kresimir.homework3.ConfirmationDialog;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.models.PokemonModel;

public class AddPokemonActivity extends AppCompatActivity implements ConfirmationDialog.OnCompleteListener {
    private static final int SELECT_IMAGE = 420;
    private static final int REQUEST_CODE_PERMISSION = 42;
    private static final String POKEMON_IMAGE = "Pokemon avatar image";
    private static final String CHANGES_MADE = "Changes made";
    private static final String DIALOG_SHOW = "Dialog show";
    private static final String IMAGE_LOCATION = "Image location";
    private boolean changesMade;
    private Uri imageUri;
    private boolean isColorChanged;

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

    @BindView(R.id.iv_add_image)
    ImageView ivPokemonImage;

    @BindView(R.id.rb_female)
    RadioButton rbGenderFemale;

    @BindView(R.id.rb_male)
    RadioButton rbGenderMale;

    @Nullable
    @BindView(R.id.tb_add_pokemon)
    Toolbar toolbar;

    @BindView(R.id.ctl_header_add_pokemon)
    CollapsingToolbarLayout ctlHeaderAddPokemon;

    @BindView(R.id.abl_header_add_pokemon)
    AppBarLayout ablHeaderAddPokemon;

    private void setToolbarTitle() {
        if (ctlHeaderAddPokemon != null) {
            ctlHeaderAddPokemon.setTitle(this.getTitle());
            ctlHeaderAddPokemon.setExpandedTitleColor(ContextCompat.getColor(this,
                    android.R.color.transparent));
        }
    }

    private void setBackArrowColor(boolean isDefaultState) {
        Drawable upArrow = ContextCompat.getDrawable(AddPokemonActivity.this,
                R.drawable.ic_arrow_back);

        if (isDefaultState) {
            upArrow.setColorFilter(ContextCompat.getColor(AddPokemonActivity.this, R.color.text_icons),
                    PorterDuff.Mode.SRC_ATOP);

            isColorChanged = true;
        }
        else {
            upArrow.setColorFilter(ContextCompat.getColor(AddPokemonActivity.this, R.color.primaryText),
                    PorterDuff.Mode.SRC_ATOP);

            isColorChanged = false;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    private void showDialog() {
        DialogFragment dialog = new ConfirmationDialog();
        Bundle args = new Bundle();

        args.putString(ConfirmationDialog.TITLE, getString(R.string.alert_dialog_title));
        args.putString(ConfirmationDialog.MESSAGE, getString(R.string.alert_dialog_message));

        dialog.setArguments(args);
        dialog.show(getFragmentManager(), DIALOG_SHOW);
    }

    private boolean checkForEmptyViews() {
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

    private boolean validateDecimalInput(EditText etContent) {
        try {
            Double.parseDouble(etContent.getText().toString());
        }
        catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    private Bitmap getImageFromImageview() {
        BitmapDrawable drawable = (BitmapDrawable) ivPokemonImage.getDrawable();
        return drawable.getBitmap();
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION);

                return false;
            }
        }
        else {
            return true;
        }
    }

    private void startImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, SELECT_IMAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pokemon);

        ButterKnife.bind(this);

        isColorChanged = false;

        setToolbarTitle();
        setBackArrowColor(true);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        ViewCompat.setTransitionName(ablHeaderAddPokemon, "EXTRA_IMAGE");
        supportPostponeEnterTransition();

        ctlHeaderAddPokemon.setStatusBarScrimColor(ContextCompat.getColor(AddPokemonActivity.this,
                R.color.primaryText));

        ablHeaderAddPokemon.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= (appBarLayout.getTotalScrollRange() / 2) && !isColorChanged) {
                    setBackArrowColor(true);
                }
                else if (Math.abs(verticalOffset) <= (appBarLayout.getTotalScrollRange() / 2)) {
                    setBackArrowColor(false);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startImagePicker();
        }
    }

    @OnClick(R.id.fab_add_image)
    public void openImage() {
        if (isStoragePermissionGranted()) {
            startImagePicker();
        }
    }

    @OnClick(R.id.btn_save_pokemon)
    public void savePokemon() {
        boolean emptyViewsExist = checkForEmptyViews();

        if (emptyViewsExist || !validateDecimalInput(etPokemonWeight) || !validateDecimalInput(etPokemonHeight)) {
            Toast toast;

            if (emptyViewsExist) {
                toast = Toast.makeText(this, R.string.empty_fields_warning, Toast.LENGTH_SHORT);
            }
            else {
                toast = Toast.makeText(this, R.string.non_numeric_value_warning, Toast.LENGTH_SHORT);
            }

            toast.show();
        }
        else {
            String pokemonName = etPokemonName.getText().toString();
            String pokemonDesc = etPokemonDescription.getText().toString();
            double pokemonHeight = Double.valueOf(etPokemonHeight.getText().toString());
            double pokemonWeight = Double.valueOf(etPokemonWeight.getText().toString());
            String category = etPokemonCategory.getText().toString();
            String abilities = etPokemonAbilities.getText().toString();
            Uri image = this.imageUri;
            String gender = (rbGenderFemale.isChecked()) ? "F" : (rbGenderMale.isChecked()) ? "M" : "-";

            PokemonModel pokemon = new PokemonModel(pokemonName, pokemonDesc, pokemonHeight,
                    pokemonWeight, category, abilities, image, gender);

            Intent intent = new Intent();
            intent.putExtra(PokemonListActivity.POKEMON, pokemon);

            setResult(PokemonListActivity.REQUEST_CODE_ADD_POKEMON, intent);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();

                ivPokemonImage.setImageBitmap(BitmapHelper.loadBitmap(this, selectedImage, false));
                this.imageUri = data.getData();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putByteArray(POKEMON_IMAGE, BitmapHelper.compressBitmap(getImageFromImageview()));
        outState.putBoolean(CHANGES_MADE, changesMade);
        outState.putParcelable(IMAGE_LOCATION, imageUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Bitmap image = BitmapHelper.decompressBitmap(savedInstanceState.getByteArray(POKEMON_IMAGE));
        ivPokemonImage.setImageBitmap(image);

        changesMade = savedInstanceState.getBoolean(CHANGES_MADE);
        imageUri = savedInstanceState.getParcelable(IMAGE_LOCATION);
    }
}
