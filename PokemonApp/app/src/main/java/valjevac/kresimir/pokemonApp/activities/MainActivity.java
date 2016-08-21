package valjevac.kresimir.pokemonApp.activities;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.fragments.AddPokemonFragment;
import valjevac.kresimir.pokemonApp.fragments.CommentsFragment;
import valjevac.kresimir.pokemonApp.fragments.ConfirmationDialogFragment;
import valjevac.kresimir.pokemonApp.fragments.PokemonDetailsFragment;
import valjevac.kresimir.pokemonApp.fragments.PokemonListFragment;
import valjevac.kresimir.pokemonApp.fragments.UserSettingsFragment;
import valjevac.kresimir.pokemonApp.helpers.PokemonHelper;
import valjevac.kresimir.pokemonApp.helpers.SharedPreferencesHelper;
import valjevac.kresimir.pokemonApp.models.Comment;
import valjevac.kresimir.pokemonApp.models.Pokemon;

public class MainActivity extends AppCompatActivity implements
        PokemonListFragment.OnFragmentInteractionListener, AddPokemonFragment.OnFragmentInteractionListener,
        ConfirmationDialogFragment.OnCompleteListener, PokemonDetailsFragment.OnFragmentInteractionListener,
        CommentsFragment.OnFragmentInteractionListener, UserSettingsFragment.OnFragmentInteractionListener {

    private static final int ORIENTATION_PORTRAIT = 1;

    private static final int ORIENTATION_LANDSCAPE = 2;

    private static final String POKEMON_LIST_FRAGMENT_TAG = "PokemonListFragment";

    public static final String ADD_POKEMON_FRAGMENT_TAG = "AddPokemonFragment";

    public static final String POKEMON_DETAILS_FRAGMENT_TAG = "PokemonDetailsFragment";

    private static final String COMMENTS_FRAGMENT_TAG = "CommentsFragment";

    private static final String USER_SETTINGS_FRAGMENT_TAG = "UserSettingFragment";

    private static final String GLIDE_MANAGER_TAG = "com.bumptech.glide.manager";

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
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        PokemonHelper.init();

        isDeviceTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        currentOrientation = getCurrentOrientation();

        if (!checkIfFragmentExists(POKEMON_LIST_FRAGMENT_TAG) && !isDeviceTablet) {
            loadFragment(PokemonListFragment.newInstance(false), POKEMON_LIST_FRAGMENT_TAG);
        }

        if (isDeviceTablet && flContainerContent != null && currentOrientation == ORIENTATION_LANDSCAPE) {
            loadFragment(AddPokemonFragment.newInstance(true), ADD_POKEMON_FRAGMENT_TAG);
            ViewCompat.setElevation(flContainerMain, 7);

            loadFragment(PokemonListFragment.newInstance(false), POKEMON_LIST_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (PokemonHelper.isCallActive()) {
            PokemonHelper.cancelRequests();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> fragments = manager.getFragments();
        
        int backstackCount = manager.getBackStackEntryCount();

        if (backstackCount == 1 || (isDeviceTablet && currentOrientation == ORIENTATION_LANDSCAPE)) {
            finish();
        }
        else {
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);

                if (fragment != null && !fragment.getTag().equals(POKEMON_LIST_FRAGMENT_TAG)
                        && !fragment.getTag().equals(GLIDE_MANAGER_TAG)) {

                    if (fragment instanceof AddPokemonFragment) {
                        AddPokemonFragment addPokemonFragment = (AddPokemonFragment) fragment;

                        if (addPokemonFragment.allowBackPressed()) {
                            removeFragmentFromStack();
                        }
                    }
                    else if (fragment instanceof CommentsFragment) {
                        removeFragmentFromStack();
                        break;
                    }
                    else {
                        removeFragmentFromStack();
                    }
                }
            }
        }
    }

    private boolean checkIfFragmentExists(String tag) {
        FragmentManager manager = getSupportFragmentManager();

        return !(manager.findFragmentByTag(tag) == null);
    }

    private void loadFragment(Fragment fragment, String tag, ImageView... imageViews) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        setFragmentTransition(tag, transaction);

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

        if (isAddableToBackstack(manager, tag)) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    private void setFragmentTransition(String tag, FragmentTransaction transaction) {

        switch (tag) {
            case ADD_POKEMON_FRAGMENT_TAG:
                transaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right);
                break;
            case POKEMON_LIST_FRAGMENT_TAG:
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                break;
            case POKEMON_DETAILS_FRAGMENT_TAG:
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                break;
            case COMMENTS_FRAGMENT_TAG:
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                break;
            case USER_SETTINGS_FRAGMENT_TAG:
                transaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right);
                break;
            default:
                transaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_right);
                break;
        }
    }

    private void setSharedElementTransition(Fragment fragment, FragmentTransaction transaction, ImageView... imageViews) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition changeTransform = TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform);

            fragment.setSharedElementEnterTransition(changeTransform);
            transaction.addSharedElement(imageViews[0], imageViews[0].getTransitionName());
        }
    }

    private boolean isAddableToBackstack(FragmentManager manager, String tag) {
        return manager.findFragmentByTag(tag) == null || (isDeviceTablet && currentOrientation == ORIENTATION_PORTRAIT);
    }

    private void removeFragmentFromStack() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.commit();

        manager.popBackStack();
    }

    private void openStarterActivity() {
        Intent intent = new Intent(MainActivity.this, StarterActivity.class);
        intent.putExtra(StarterActivity.SPLASH_ANIMATION, false);
        startActivity(intent);

        finish();
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }

    @Override
    public void onShowPokemonDetailsClick(Pokemon pokemon, ImageView imageView) {
        if (checkIfFragmentExists(POKEMON_DETAILS_FRAGMENT_TAG) && isDeviceTablet) {
            removeFragmentFromStack();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadFragment(PokemonDetailsFragment.newInstance(pokemon, imageView.getTransitionName()), POKEMON_DETAILS_FRAGMENT_TAG, imageView);
        }
        else {
            loadFragment(PokemonDetailsFragment.newInstance(pokemon), POKEMON_DETAILS_FRAGMENT_TAG);
        }
    }

    @Override
    public void onPokemonAdded(Pokemon pokemon) {
        if (!isDeviceTablet) {
            removeFragmentFromStack();
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(POKEMON_LIST_FRAGMENT_TAG);

        Bundle args = new Bundle();
        args.putParcelable(PokemonListFragment.POKEMON, pokemon);
        fragment.getArguments().putAll(args);

        loadFragment(fragment, POKEMON_LIST_FRAGMENT_TAG);
    }

    @Override
    public void onDetailsHomePressed() {
        removeFragmentFromStack();
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

                removeFragmentFromStack();
            }
        }
    }

    @Override
    public void onAddPokemonClick() {
        if (checkIfFragmentExists(POKEMON_DETAILS_FRAGMENT_TAG)) {
            removeFragmentFromStack();
        }

        loadFragment(AddPokemonFragment.newInstance(isDeviceTablet), ADD_POKEMON_FRAGMENT_TAG);
    }

    @Override
    public void onShowAllCommentsPressed(String pokemonName, ArrayList<Comment> comments, String nextPage, int pokemonId) {
        loadFragment(CommentsFragment.newInstance(pokemonName, comments, nextPage, pokemonId), COMMENTS_FRAGMENT_TAG);
    }

    @Override
    public void onLogoutClick() {
        SharedPreferencesHelper.logout();

        openStarterActivity();
    }

    @Override
    public void onUserSettingsClick() {
        loadFragment(UserSettingsFragment.newInstance(), USER_SETTINGS_FRAGMENT_TAG);
    }

    @Override
    public void onCommentsHomePressed() {
        removeFragmentFromStack();
    }

    @Override
    public void onSettingsHomePressed() {
        removeFragmentFromStack();
    }

    @Override
    public void onAccountDeleted() {
        openStarterActivity();
    }
}
