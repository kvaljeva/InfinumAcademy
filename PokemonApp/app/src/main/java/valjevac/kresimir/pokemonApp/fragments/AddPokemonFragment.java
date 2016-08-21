package valjevac.kresimir.pokemonApp.fragments;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import valjevac.kresimir.pokemonApp.PokemonApplication;
import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.activities.MainActivity;
import valjevac.kresimir.pokemonApp.custom.ProgressView;
import valjevac.kresimir.pokemonApp.enums.ItemsType;
import valjevac.kresimir.pokemonApp.enums.PermissionType;
import valjevac.kresimir.pokemonApp.enums.SetFocus;
import valjevac.kresimir.pokemonApp.helpers.BitmapHelper;
import valjevac.kresimir.pokemonApp.models.Pokemon;
import valjevac.kresimir.pokemonApp.mvp.presenters.AddPokemonPresenter;
import valjevac.kresimir.pokemonApp.mvp.presenters.impl.AddPokemonPresenterImpl;
import valjevac.kresimir.pokemonApp.mvp.views.AddPokemonView;

public class AddPokemonFragment extends Fragment implements AddPokemonView {

    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    private AddPokemonPresenter presenter;

    private static final int SELECT_IMAGE = 420;

    private static final int TAKEN_PHOTO = 24;

    private static final int PERMISSION_REQUEST_CODE = 42;

    private static final int DIALOG_RESULT = 4;

    private static final String CHANGES_MADE = "ChangesMade";

    private static final String DIALOG_SHOW = "DialogShow";

    private static final String IMAGE_LOCATION = "ImageLocation";

    private static final String FORMAT_TYPE_IMAGE = "image/*";

    private static final String IS_DEVICE_TABLET = "IsTablet";

    private static final String IS_PHOTO_LAYOUT_VISIBLE = "PhotoLayoutVisibility";

    private static final double VERTICAL_OFFSET_CENTER = 1.55;

    private boolean changesMade;

    private Uri imageUri;

    private boolean isColorChanged;

    private boolean isTabletView;

    private boolean photoOptionsVisible;

    @BindView(R.id.et_pokemon_name)
    EditText etPokemonName;

    @BindView(R.id.et_pokemon_desc)
    EditText etPokemonDescription;

    @BindView(R.id.et_pokemon_height)
    EditText etPokemonHeight;

    @BindView(R.id.et_pokemon_weight)
    EditText etPokemonWeight;

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

    @BindView(R.id.pv_add_pokemon)
    ProgressView progressView;

    @BindView(R.id.tv_types_list)
    TextView tvTypeList;

    @BindView(R.id.tv_moves_list)
    TextView tvMovesList;

    @BindView(R.id.ll_photo_options_container)
    LinearLayout llPhotoOptionsContainer;

    @BindView(R.id.btn_choose_photo)
    Button btnChoosePhoto;

    @BindView(R.id.btn_take_photo)
    Button btnTakePhoto;

    private Animation rotateForward, rotateBackwards;

    private boolean isFabActive;

    public AddPokemonFragment() { }

    public interface OnFragmentInteractionListener {

        void onPokemonAdded(Pokemon pokemon);

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new AddPokemonPresenterImpl(this);

        rotateForward = AnimationUtils.loadAnimation(PokemonApplication.getAppContext(), R.anim.rotate_forward);
        rotateBackwards = AnimationUtils.loadAnimation(PokemonApplication.getAppContext(), R.anim.rotate_backwards);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_pokemon, container, false);
        unbinder = ButterKnife.bind(this, view);

        isColorChanged = false;
        photoOptionsVisible = false;

        if (getArguments() != null) {
            Bundle args = getArguments();

            isTabletView = args.getBoolean(IS_DEVICE_TABLET);
        }

        if (savedInstanceState != null) {
            changesMade = savedInstanceState.getBoolean(CHANGES_MADE);
            imageUri = savedInstanceState.getParcelable(IMAGE_LOCATION);
            photoOptionsVisible = savedInstanceState.getBoolean(IS_PHOTO_LAYOUT_VISIBLE);

            if (imageUri != null && !TextUtils.isEmpty(imageUri.toString())) {
                BitmapHelper.loadBitmap(ivPokemonImage, imageUri.toString(), false);
            }
        }

        if (photoOptionsVisible) {
            showImageOptions();
            setFabActiveState();
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
                    if (Math.abs(verticalOffset) >= (appBarLayout.getTotalScrollRange() /
                            VERTICAL_OFFSET_CENTER) && !isColorChanged) {

                        setBackArrowColor(true, (MainActivity) getActivity());
                    }
                    else if (Math.abs(verticalOffset) <= (appBarLayout.getTotalScrollRange() / VERTICAL_OFFSET_CENTER)) {
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

        if (presenter != null) {
            presenter.cancel();
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

        if (requestCode == SELECT_IMAGE || requestCode == TAKEN_PHOTO) {
            if (resultCode == MainActivity.RESULT_OK) {
                Uri selectedImage = data.getData();
                BitmapHelper.loadBitmap(ivPokemonImage, selectedImage.toString(), false);

                imageUri = selectedImage;
                changesMade = true;

                showPhotoOptions();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(CHANGES_MADE, changesMade);
        outState.putParcelable(IMAGE_LOCATION, imageUri);
        outState.putBoolean(IS_PHOTO_LAYOUT_VISIBLE, photoOptionsVisible);
    }


    @Override
    public void onPokemonAddSuccess(Pokemon pokemon) {
        listener.onPokemonAdded(pokemon);
    }

    @Override
    public void showProgress() {
        progressView.show();
    }

    @Override
    public void hideProgress() {
        progressView.hide();
    }

    @Override
    public void showMessage(@StringRes int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void focusView(SetFocus focus) {
        switch (focus) {
            case PokemonName:
                etPokemonName.requestFocus();
                break;
            case PokemonWeight:
                etPokemonWeight.requestFocus();
                break;
            case PokemonHeight:
                etPokemonHeight.requestFocus();
                break;
            case PokemonDescription:
                etPokemonDescription.requestFocus();
                break;
            default: break;
        }
    }

    @Override
    public void getMultipleSelectionItems(String[] itemsArray, boolean[] checkedItems, String title, ItemsType itemsType) {
        createSelectionDialog(itemsArray, checkedItems, title, itemsType);
    }

    @Override
    public void displaySelectedItems(String items, ItemsType itemsType) {
        items = (TextUtils.isEmpty(items)) ? getString(R.string.not_assigned) : items;

        switch (itemsType) {
            case Moves:
                tvMovesList.setText(items);
                break;
            case Types:
                tvTypeList.setText(items);
                break;
            default: break;
        }
    }

    @Override
    public void checkForPermission(String[] permissions, PermissionType permissionType) {
        if (askForPermission(permissions) && permissionType == PermissionType.Camera) {
            startCamera();
        }
        else if (askForPermission(permissions) && permissionType == PermissionType.ExternalStorage) {
            startImagePicker();
        }
    }

    @Override
    public void clearInputViews() {
        clearInputViews(rlActivityBody);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImagePicker();
            }
        }
        else if (grantResults.length > 1) {
            boolean arePermissionGranted = true;

            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    arePermissionGranted = false;
                    break;
                }
            }

            if (arePermissionGranted) {
                startCamera();
            }
        }
    }

    @OnClick(R.id.fab_add_image)
    public void onShowImageOptionsClick() {
        showPhotoOptions();
    }

    @OnClick(R.id.btn_choose_photo)
    public void onChooseImagePick() {
        presenter.checkForPermission(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                PermissionType.ExternalStorage);
    }

    @OnClick(R.id.btn_take_photo)
    public void onTakePhotoClick() {
        presenter.checkForPermission(new String [] { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE },
                PermissionType.Camera);
    }

    @OnClick(R.id.btn_save_pokemon)
    public void onSavePokemonClick() {
        String name = etPokemonName.getText().toString();
        String description = etPokemonDescription.getText().toString();
        String height = etPokemonHeight.getText().toString();
        String weight = etPokemonWeight.getText().toString();
        int gender = (rbGenderMale.isChecked()) ? 1 : 2;

        presenter.addPokemon(name, description, height, weight, imageUri, gender);
    }

    @OnTextChanged(R.id.et_pokemon_desc)
    public void notifyDescChange(CharSequence charSequence) {
        changesMade = !TextUtils.isEmpty(charSequence);
    }

    @OnTextChanged(R.id.et_pokemon_name)
    public void notifyNameChange(CharSequence charSequence) {
        changesMade = !TextUtils.isEmpty(charSequence);
    }

    @OnTextChanged(R.id.et_pokemon_height)
    public void notifyHeightChange(CharSequence charSequence) {
        changesMade = !TextUtils.isEmpty(charSequence);
    }

    @OnTextChanged(R.id.et_pokemon_weight)
    public void notifyWeightChange(CharSequence charSequence) {
        changesMade = !TextUtils.isEmpty(charSequence);
    }

    @OnClick(R.id.tv_moves_list)
    public void selectPokemonMoves() {
        presenter.getPokemonMoves();
    }

    @OnClick(R.id.tv_types_list)
    public void selectPokemonType() {
        presenter.getPokemonTypes();
    }

    private void createSelectionDialog(final String[] itemsArray, final boolean[] checkedItems, final String title, final ItemsType itemsType) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        builder.setMultiChoiceItems(itemsArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                checkedItems[which] = isChecked;
            }
        });
        builder.setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                presenter.setSelectedItems(itemsArray, checkedItems, itemsType);
            }
        });
        builder.setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void showPhotoOptions() {

        if (ctlHeaderAddPokemon != null) {
            final int x = ctlHeaderAddPokemon.getRight();
            final int y = ctlHeaderAddPokemon.getBottom();

            final int hypotenuse = (int) Math.hypot(ctlHeaderAddPokemon.getWidth(), ctlHeaderAddPokemon.getHeight());

            llPhotoOptionsContainer.post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (!photoOptionsVisible) {
                            setFabActiveState();

                            CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) llPhotoOptionsContainer.getLayoutParams();

                            params.height = ctlHeaderAddPokemon.getHeight();
                            llPhotoOptionsContainer.setLayoutParams(params);

                            Animator animator = ViewAnimationUtils.createCircularReveal(llPhotoOptionsContainer, x, y, 0, hypotenuse);
                            animator.setDuration(400);

                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    btnChoosePhoto.setVisibility(View.VISIBLE);
                                    btnTakePhoto.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });

                            llPhotoOptionsContainer.setVisibility(View.VISIBLE);

                            animator.start();
                            photoOptionsVisible = true;
                        }
                        else {
                            setFabClosedState();

                            Animator animator = ViewAnimationUtils.createCircularReveal(llPhotoOptionsContainer, x, y, hypotenuse, 0);
                            animator.setDuration(400);

                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    hideImageOptions(false);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });

                            animator.start();
                            photoOptionsVisible = false;
                        }
                    }
                }
            });
        }
    }

    private void setFabActiveState() {
        fabAddImage.startAnimation(rotateForward);
        fabAddImage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));
        fabAddImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        isFabActive = true;
    }

    private void setFabClosedState() {
        fabAddImage.startAnimation(rotateBackwards);
        fabAddImage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.colorPrimary)));
        fabAddImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white));

        isFabActive = false;
    }

    private void showImageOptions() {
        llPhotoOptionsContainer.setVisibility(View.VISIBLE);
        btnChoosePhoto.setVisibility(View.VISIBLE);
        btnTakePhoto.setVisibility(View.VISIBLE);
    }

    private void hideImageOptions(boolean hideContainerOnly) {
        llPhotoOptionsContainer.setVisibility(View.GONE);

        if (!hideContainerOnly) {
            btnChoosePhoto.setVisibility(View.GONE);
            btnTakePhoto.setVisibility(View.GONE);
        }
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            MainActivity mainActivity = (MainActivity) getActivity();

            toolbar.setTitle(R.string.add_pokemon_toolbar_title);

            mainActivity.setSupportActionBar(toolbar);

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
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.white),
                    PorterDuff.Mode.SRC_ATOP);

            isColorChanged = true;

            if (isFabActive) {
                setFabClosedState();
                hideImageOptions(true);
                photoOptionsVisible = false;
            }
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

    private boolean askForPermission(String[] permissions) {
        boolean permissionsGranted = true;

        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions ) {
                if (!(ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                    permissionsGranted = false;
                }
            }
        }
        else {
            permissionsGranted = true;
        }

        return permissionsGranted;
    }

    private void startImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(FORMAT_TYPE_IMAGE);

        startActivityForResult(intent, SELECT_IMAGE);
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, TAKEN_PHOTO);
    }
}