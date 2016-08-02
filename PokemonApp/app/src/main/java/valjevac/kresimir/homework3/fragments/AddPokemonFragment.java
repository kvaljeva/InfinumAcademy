package valjevac.kresimir.homework3.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.network.ApiManager;

public class AddPokemonFragment extends Fragment {
    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    private static final int SELECT_IMAGE = 420;

    private static final int REQUEST_CODE_PERMISSION = 42;

    private static final int DIALOG_RESULT = 4;

    private static final String CHANGES_MADE = "ChangesMade";

    private static final String DIALOG_SHOW = "DialogShow";

    private static final String IMAGE_LOCATION = "ImageLocation";

    private static final String FORMAT_TYPE_IMAGE = "image/*";

    private static final String MEDIA_TYPE = "application/image";

    private static final String IS_DEVICE_TABLET = "IsTablet";

    private boolean changesMade;

    private Uri imageUri;

    private boolean isColorChanged;

    private boolean isTabletView;

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

    @BindView(R.id.fab_add_image)
    FloatingActionButton fabAddImage;

    @Nullable
    @BindView(R.id.tb_add_pokemon)
    Toolbar toolbar;

    @Nullable
    @BindView(R.id.ctl_header_add_pokemon)
    CollapsingToolbarLayout ctlHeaderAddPokemon;

    @Nullable
    @BindView(R.id.abl_header_add_pokemon)
    AppBarLayout ablHeaderAddPokemon;

    @Nullable
    @BindView(R.id.sv_body_container)
    NestedScrollView svBodyContainer;

    @BindView(R.id.rl_add_pokemon_progress_container)
    RelativeLayout rlProgressContainer;

    Call<BaseResponse<Data<Pokemon>>> insertPokemonCall;

    public AddPokemonFragment() { }

    public interface OnFragmentInteractionListener {

        void onPokemonAdded();

        void onAddHomePressed();
    }

    public static AddPokemonFragment newInstance() {

        return new AddPokemonFragment();
    }

    public static AddPokemonFragment newInstance(boolean isDeviceTablet) {

        AddPokemonFragment fragment = new AddPokemonFragment();

        Bundle args = new Bundle();
        args.putBoolean(IS_DEVICE_TABLET, isDeviceTablet);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_pokemon, container, false);
        unbinder = ButterKnife.bind(this, view);

        isColorChanged = false;

        if (getArguments() != null) {
            Bundle args = getArguments();

            isTabletView = args.getBoolean(IS_DEVICE_TABLET);
        }

        if (savedInstanceState != null) {
            changesMade = savedInstanceState.getBoolean(CHANGES_MADE);
            imageUri = savedInstanceState.getParcelable(IMAGE_LOCATION);

            if (imageUri != null && !imageUri.toString().isEmpty()) {
                BitmapHelper.loadBitmap(ivPokemonImage, imageUri.toString(), false);
            }
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        setUpToolbar();

        changesMade = false;

        if (ablHeaderAddPokemon != null) {
            ablHeaderAddPokemon.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if ((Math.abs(verticalOffset) + 10) >= (appBarLayout.getTotalScrollRange() / 1.5) && !isColorChanged) {
                        setBackArrowColor(true, (MainActivity) getActivity());
                    }
                    else if (Math.abs(verticalOffset) <= (appBarLayout.getTotalScrollRange() / 1.5)) {
                        setBackArrowColor(false, (MainActivity) getActivity());
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener.");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (insertPokemonCall != null) {
            insertPokemonCall.cancel();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (listener != null) {
            listener = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE) {
            if (resultCode == MainActivity.RESULT_OK) {
                Uri selectedImage = data.getData();

                BitmapHelper.loadBitmap(ivPokemonImage, selectedImage.toString(), false);
                imageUri = selectedImage;

                changesMade = true;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(CHANGES_MADE, changesMade);
        outState.putParcelable(IMAGE_LOCATION, imageUri);
    }

    private void setUpToolbar() {
        MainActivity mainActivity = (MainActivity) getActivity();

        if (toolbar != null) {
            mainActivity.setSupportActionBar(toolbar);

            toolbar.setTitle(R.string.add_pokemon_toolbar_title);

            if (mainActivity.getSupportActionBar() != null) {
                mainActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            setToolbarTitle();
            setBackArrowColor(true, mainActivity);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAddHomePressed();
                }
            });
        }
    }

    private void setToolbarTitle() {
        if (ctlHeaderAddPokemon != null) {
            ctlHeaderAddPokemon.setTitle(getString(R.string.add_pokemon_toolbar_title));
            ctlHeaderAddPokemon.setExpandedTitleColor(ContextCompat.getColor(getActivity(),
                    android.R.color.transparent));
        }
    }

    private void setBackArrowColor(boolean isDefaultState, MainActivity activity) {
        Drawable upArrow = ContextCompat.getDrawable(getActivity(),
                R.drawable.ic_arrow_back);

        if (isDefaultState) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.text_icons),
                    PorterDuff.Mode.SRC_ATOP);

            isColorChanged = true;
        }
        else {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.primaryText),
                    PorterDuff.Mode.SRC_ATOP);

            isColorChanged = false;
        }

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    private void showDialog() {
        DialogFragment dialog = new ConfirmationDialogFragment();
        Bundle args = new Bundle();

        args.putString(ConfirmationDialogFragment.TITLE, getString(R.string.alert_dialog_title));
        args.putString(ConfirmationDialogFragment.MESSAGE, getString(R.string.alert_dialog_message));

        dialog.setArguments(args);
        dialog.setTargetFragment(this, DIALOG_RESULT);
        dialog.show(getActivity().getSupportFragmentManager(), DIALOG_SHOW);
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

    private void clearInputViews(ViewGroup group) {

        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);

            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                editText.setText("");
            }
            else if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                radioButton.setChecked(false);
            }

            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0)) {
                clearInputViews((ViewGroup) view);
            }
        }

        this.imageUri = null;
        BitmapHelper.loadResourceBitmap(ivPokemonImage, R.drawable.ic_person_details, false);
    }

    private boolean validateDecimalInput(EditText etContent) {
        // Promijenjeno Kocu za dusu
        Pattern pattern = Pattern.compile("^(-?0[.]\\d+)$|^(-?[1-9]+\\d*([.]\\d+)?)$|^0$");
        String content = etContent.getText().toString();

        return pattern.matcher(content).matches();
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
        intent.setType(FORMAT_TYPE_IMAGE);

        startActivityForResult(intent, SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startImagePicker();
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.enter_right);
        }
        else {
            if (isTabletView) {
                return AnimationUtils.loadAnimation(getActivity(), R.anim.exit_left);
            }

            return AnimationUtils.loadAnimation(getActivity(), R.anim.exit_right);
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

        if (!NetworkHelper.isNetworkAvailable()) {
            Toast.makeText(getActivity(), getString(R.string.no_internet_conn),
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if (emptyViewsExist || !validateDecimalInput(etPokemonWeight) || !validateDecimalInput(etPokemonHeight)) {
            Toast toast;

            if (emptyViewsExist) {
                toast = Toast.makeText(getActivity(), R.string.empty_fields_warning, Toast.LENGTH_SHORT);
            }
            else {
                toast = Toast.makeText(getActivity(), R.string.non_numeric_value_warning, Toast.LENGTH_SHORT);
            }

            toast.show();
        }
        else {

            showProgress(true);

            String pokemonName = etPokemonName.getText().toString();
            String pokemonDesc = etPokemonDescription.getText().toString();
            float pokemonHeight = Float.valueOf(etPokemonHeight.getText().toString());
            float pokemonWeight = Float.valueOf(etPokemonWeight.getText().toString());
            String category = etPokemonCategory.getText().toString();
            String abilities = etPokemonAbilities.getText().toString();
            Uri image = (this.imageUri == null) ? BitmapHelper.getResourceUri(R.drawable.ic_person_details) : this.imageUri;
            int gender = (rbGenderFemale.isChecked()) ? 2 : 1;

            Pokemon pokemon = new Pokemon(pokemonName, pokemonDesc, pokemonHeight,
                    pokemonWeight, category, abilities, image.toString(), gender);

            insertPokemon(pokemon);
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

    public boolean allowBackPressed() {
        if (changesMade) {
            showDialog();
            return false;
        }

        return true;
    }

    public void clearUserData() {
        clearInputViews(rlActivityBody);
    }

    private String getFilePath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            return null;
        }

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String path = cursor.getString(columnIndex);

        cursor.close();

        return path;
    }

    private void showProgress(boolean isVisible) {

        if (isVisible) {
            if (ablHeaderAddPokemon != null && svBodyContainer != null && fabAddImage != null) {

                ablHeaderAddPokemon.setVisibility(View.GONE);
                svBodyContainer.setVisibility(View.GONE);
                fabAddImage.setVisibility(View.GONE);
            }

            rlProgressContainer.setVisibility(View.VISIBLE);
        }
        else {
            if (ablHeaderAddPokemon != null && svBodyContainer != null && fabAddImage != null) {

                ablHeaderAddPokemon.setVisibility(View.VISIBLE);
                svBodyContainer.setVisibility(View.VISIBLE);
                fabAddImage.setVisibility(View.VISIBLE);
            }

            rlProgressContainer.setVisibility(View.GONE);
        }
    }

    private void insertPokemon(Pokemon pokemon) {
        int[] moves = new int[0];
        int[] category = new int[0];
        
        String filePath = getFilePath(this.imageUri);
        File imageFile;
        RequestBody body = null;

        if (filePath != null) {

            imageFile = new File(filePath);
            body = RequestBody.create(MediaType.parse(MEDIA_TYPE), imageFile);
        }

        insertPokemonCall = ApiManager.getService().insertPokemon(
                pokemon.getName(),
                pokemon.getHeight(),
                pokemon.getWeight(),
                pokemon.getGender(),
                true,
                pokemon.getDescription(),
                category,
                moves,
                body
        );

        insertPokemonCall.enqueue(new Callback<BaseResponse<Data<Pokemon>>>() {
            @Override
            public void onResponse(Call<BaseResponse<Data<Pokemon>>> call, Response<BaseResponse<Data<Pokemon>>> response) {
                Toast.makeText(getActivity(), R.string.pokemon_saved, Toast.LENGTH_SHORT).show();

                clearInputViews(rlActivityBody);
                listener.onPokemonAdded();
            }

            @Override
            public void onFailure(Call<BaseResponse<Data<Pokemon>>> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.pokemon_save_failed, Toast.LENGTH_SHORT).show();

                showProgress(false);
            }
        });
    }
}
