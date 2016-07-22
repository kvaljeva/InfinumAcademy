package valjevac.kresimir.homework3.activities;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.ConfirmationDialog;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.fragments.AddPokemonFragment;
import valjevac.kresimir.homework3.fragments.PokemonDetailsFragment;
import valjevac.kresimir.homework3.fragments.PokemonListFragment;
import valjevac.kresimir.homework3.models.PokemonModel;

public class PokemonListActivity extends AppCompatActivity implements
        PokemonListFragment.OnFragmentInteractionListener, AddPokemonFragment.OnFragmentInteractionListener,
        ConfirmationDialog.OnCompleteListener, PokemonDetailsFragment.OnFragmentInteractionListener {

    private static final String POKEMON_LIST_FRAGMENT_TAG = "PokemonListFragment";
    public static final String ADD_POKEMON_FRAGMENT_TAG = "AddPokemonFragment";
    private static final String POKEMON_DETAILS_FRAGMENT_TAG = "PokemonDetailsFragment";

    @Nullable
    @BindView(R.id.fragmentContainer)
    FrameLayout flFragmentContainer;

    @BindView(R.id.fl_container_main)
    FrameLayout flContainerMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        ButterKnife.bind(this);

        if (!checkIfFragmentExists(POKEMON_LIST_FRAGMENT_TAG)) {
            loadFragment(PokemonListFragment.newInstance(false), POKEMON_LIST_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        }
        else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(ADD_POKEMON_FRAGMENT_TAG);

            if (fragment instanceof AddPokemonFragment) {
                if (((AddPokemonFragment) fragment).allowBackPressed()) {
                    removeFragmentFromStack(fragment.getTag());
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

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fl_container_main, fragment, tag);

        if (manager.findFragmentByTag(tag) == null) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    private void removeFragmentFromStack(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.remove(manager.findFragmentByTag(tag));
        transaction.commit();

        manager.popBackStack();
    }

    @Override
    public void onAddPokemonClick() {
        loadFragment(AddPokemonFragment.newInstance(), ADD_POKEMON_FRAGMENT_TAG);
    }

    @Override
    public void onShowPokemonDetailsClick(PokemonModel pokemon) {
        loadFragment(PokemonDetailsFragment.newInstance(pokemon), POKEMON_DETAILS_FRAGMENT_TAG);
    }

    @Override
    public void onPokemonAdded(int requestCode, PokemonModel pokemon) {
        removeFragmentFromStack(ADD_POKEMON_FRAGMENT_TAG);
        loadFragment(PokemonListFragment.newInstance(pokemon), POKEMON_LIST_FRAGMENT_TAG);
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
                removeFragmentFromStack(ADD_POKEMON_FRAGMENT_TAG);
            }
        }
    }
}
