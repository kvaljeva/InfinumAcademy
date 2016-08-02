package valjevac.kresimir.homework3.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.fragments.AddPokemonFragment;
import valjevac.kresimir.homework3.fragments.ConfirmationDialogFragment;
import valjevac.kresimir.homework3.fragments.PokemonDetailsFragment;
import valjevac.kresimir.homework3.fragments.PokemonListFragment;
import valjevac.kresimir.homework3.fragments.ProgressLoadFragment;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;
import valjevac.kresimir.homework3.models.Pokemon;

public class PokemonListActivity extends AppCompatActivity implements
        PokemonListFragment.OnFragmentInteractionListener, AddPokemonFragment.OnFragmentInteractionListener,
        ConfirmationDialogFragment.OnCompleteListener, PokemonDetailsFragment.OnFragmentInteractionListener {

    private static final int ORIENTATION_PORTRAIT = 1;

    private static final int ORIENTATION_LANDSCAPE = 2;

    private static final String POKEMON_LIST_FRAGMENT_TAG = "PokemonListFragment";

    public static final String ADD_POKEMON_FRAGMENT_TAG = "AddPokemonFragment";

    private static final String POKEMON_DETAILS_FRAGMENT_TAG = "PokemonDetailsFragment";

    private static final String PROGRESS_LOAD_FRAGMENT_TAG = "ProgressLoadFragment";

    private static final String PROGRESS_LOAD_TITLE = "Pokemon";

    private boolean isDeviceTablet;

    private int currentOrientation;

    @Nullable
    @BindView(R.id.fl_container_content)
    FrameLayout flContainerContent;

    @BindView(R.id.fl_container_main)
    FrameLayout flContainerMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        ButterKnife.bind(this);

        isDeviceTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        currentOrientation = getCurrentOrientation();

        if (!checkIfFragmentExists(POKEMON_LIST_FRAGMENT_TAG) && !isDeviceTablet) {
            loadFragment(PokemonListFragment.newInstance(false), POKEMON_LIST_FRAGMENT_TAG);

            if (NetworkHelper.isNetworkAvailable()) {
                loadFragment(ProgressLoadFragment.newInstance(getString(R.string.progress_load_description),
                        PROGRESS_LOAD_TITLE), PROGRESS_LOAD_FRAGMENT_TAG);
            }
        }

        if (isDeviceTablet) {
            if (isDeviceTablet && flContainerContent != null && currentOrientation == ORIENTATION_LANDSCAPE) {
                loadFragment(AddPokemonFragment.newInstance(true), ADD_POKEMON_FRAGMENT_TAG);
                ViewCompat.setElevation(flContainerMain, 7);
            }

            // Load initial fragment every time
            loadFragment(PokemonListFragment.newInstance(false), POKEMON_LIST_FRAGMENT_TAG);

            if (NetworkHelper.isNetworkAvailable()) {
                loadFragment(ProgressLoadFragment.newInstance(getString(R.string.progress_load_description),
                        PROGRESS_LOAD_TITLE), PROGRESS_LOAD_FRAGMENT_TAG);
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(ADD_POKEMON_FRAGMENT_TAG);

        int backstackCount = manager.getBackStackEntryCount();

        if (backstackCount == 1 || isDeviceTablet) {
            if (backstackCount == 1 || currentOrientation == ORIENTATION_LANDSCAPE) {
                finish();
            }
            else {
                if (fragment instanceof  AddPokemonFragment) {
                    AddPokemonFragment addPokemonFragment = (AddPokemonFragment) fragment;

                    if (addPokemonFragment.allowBackPressed()) {
                        removeFragmentFromStack(ADD_POKEMON_FRAGMENT_TAG);
                    }
                }
                else {
                    removeFragmentFromStack(POKEMON_DETAILS_FRAGMENT_TAG);
                }
            }
        }
        else {
            if (fragment instanceof  AddPokemonFragment) {
                AddPokemonFragment addPokemonFragment = (AddPokemonFragment) fragment;

                if (addPokemonFragment.allowBackPressed()) {
                    removeFragmentFromStack(ADD_POKEMON_FRAGMENT_TAG);
                }
            }
            else {
                removeFragmentFromStack(POKEMON_DETAILS_FRAGMENT_TAG);
            }
        }
    }

    private boolean checkIfFragmentExists(String tag) {
        FragmentManager manager = getSupportFragmentManager();

        return !(manager.findFragmentByTag(tag) == null);
    }

    private void loadFragment(Fragment fragment, String tag, ImageView... imageView) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (tag.equals(POKEMON_DETAILS_FRAGMENT_TAG)) {

            if (checkIfFragmentExists(POKEMON_LIST_FRAGMENT_TAG)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && imageView.length > 0) {

//                    Fragment listFragment = manager.findFragmentByTag(POKEMON_LIST_FRAGMENT_TAG);
//                    Transition changeTransform = TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform);
//
//                    listFragment.setSharedElementReturnTransition(changeTransform);
//                    listFragment.setExitTransition(changeTransform);
//
//                    fragment.setSharedElementEnterTransition(changeTransform);
//                    fragment.setEnterTransition(changeTransform);
//
//                    ImageView ivPokemonImage = (imageView.length > 0) ? imageView[0] : null;
//
//                    transaction.addSharedElement(ivPokemonImage, getString(R.string.details_transit));
                }
            }
        }

        if (tag.equals(POKEMON_LIST_FRAGMENT_TAG) || !isDeviceTablet) {
            transaction.replace(R.id.fl_container_main, fragment, tag);
        }
        else {
            if (currentOrientation == ORIENTATION_PORTRAIT) {
                transaction.replace(R.id.fl_container_main, fragment, tag);
            }
            else {
                transaction.replace(R.id.fl_container_content, fragment, tag);
            }
        }

        if (manager.findFragmentByTag(tag) == null || (isDeviceTablet
                && currentOrientation == ORIENTATION_PORTRAIT)) {

            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    private void removeFragmentFromStack(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (tag.equals(PROGRESS_LOAD_FRAGMENT_TAG)) {
            transaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_out);
        }

        transaction.remove(manager.findFragmentByTag(tag));
        transaction.commit();

        manager.popBackStack();
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }

    @Override
    public void onShowPokemonDetailsClick(Pokemon pokemon) {
        if (checkIfFragmentExists(POKEMON_DETAILS_FRAGMENT_TAG) && isDeviceTablet) {
            removeFragmentFromStack(POKEMON_DETAILS_FRAGMENT_TAG);
        }

        loadFragment(PokemonDetailsFragment.newInstance(pokemon, isDeviceTablet), POKEMON_DETAILS_FRAGMENT_TAG);
    }

    @Override
    public void onPokemonAdded() {
        if (!isDeviceTablet) {
            removeFragmentFromStack(ADD_POKEMON_FRAGMENT_TAG);
        }
        else {
            if (currentOrientation == ORIENTATION_LANDSCAPE) {
                removeFragmentFromStack(POKEMON_LIST_FRAGMENT_TAG);
            }
        }

        loadFragment(PokemonListFragment.newInstance(isDeviceTablet), POKEMON_LIST_FRAGMENT_TAG);

        if (NetworkHelper.isNetworkAvailable()) {
            loadFragment(ProgressLoadFragment.newInstance(getString(R.string.progress_load_description),
                    PROGRESS_LOAD_TITLE), PROGRESS_LOAD_FRAGMENT_TAG);
        }
    }

    @Override
    public void onDetailsHomePressed() {
        removeFragmentFromStack(POKEMON_DETAILS_FRAGMENT_TAG);
    }

    @Override
    public void onAddHomePressed() {
        onBackPressed();
    }

    @Override
    public void onComplete(boolean confirmation, Fragment fragment) {
        if (confirmation) {
            if (fragment instanceof AddPokemonFragment) {
                if (isDeviceTablet && currentOrientation == ORIENTATION_LANDSCAPE) {
                    finish();
                }

                ((AddPokemonFragment) fragment).clearUserData();

                removeFragmentFromStack(ADD_POKEMON_FRAGMENT_TAG);
            }
        }
    }

    @Override
    public void onPokemonListLoad(boolean isSuccess) {
        if (isSuccess) {
            if (checkIfFragmentExists(PROGRESS_LOAD_FRAGMENT_TAG)) {
                removeFragmentFromStack(PROGRESS_LOAD_FRAGMENT_TAG);
            }
        }
        else {
            if (!NetworkHelper.isNetworkAvailable()) {
                Toast.makeText(PokemonListActivity.this, getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
            }

            removeFragmentFromStack(PROGRESS_LOAD_FRAGMENT_TAG);
        }
    }

    @Override
    public void onAddPokemonClick() {
        if (checkIfFragmentExists(POKEMON_DETAILS_FRAGMENT_TAG)) {
            removeFragmentFromStack(POKEMON_DETAILS_FRAGMENT_TAG);
        }

        loadFragment(AddPokemonFragment.newInstance(isDeviceTablet), ADD_POKEMON_FRAGMENT_TAG);
    }

    @Override
    public void onLogoutClick() {

        SharedPreferencesHelper.setInt(0, SharedPreferencesHelper.USER_ID);
        SharedPreferencesHelper.setString("", SharedPreferencesHelper.AUTH_TOKEN);
        SharedPreferencesHelper.setString("", SharedPreferencesHelper.USER);
        SharedPreferencesHelper.setString("", SharedPreferencesHelper.EMAIL);

        Intent intent = new Intent(PokemonListActivity.this, LoginActivity.class);
        startActivity(intent);

        finish();
    }
}
