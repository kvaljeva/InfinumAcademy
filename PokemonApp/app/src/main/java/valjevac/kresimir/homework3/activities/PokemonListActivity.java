package valjevac.kresimir.homework3.activities;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

    private static final int ORIENTATION_PORTRAIT = 1;
    private static final int ORIENTATION_LANDSCAPE = 2;
    private static final String POKEMON_LIST_FRAGMENT_TAG = "PokemonListFragment";
    public static final String ADD_POKEMON_FRAGMENT_TAG = "AddPokemonFragment";
    private static final String POKEMON_DETAILS_FRAGMENT_TAG = "PokemonDetailsFragment";
    private boolean isDeviceTablet;
    private int currentOrientation;

    @Nullable
    @BindView(R.id.fl_container_content)
    FrameLayout flContainerContent;

    @BindView(R.id.fl_container_main)
    FrameLayout flContainerMain;

    @Nullable
    @BindView(R.id.tb_pokemon_list)
    Toolbar toolbar;

    @BindView(R.id.nvDrawer)
    NavigationView navigationDrawer;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        ButterKnife.bind(this);

        isDeviceTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        currentOrientation = getCurrentOrientation();

        if (!checkIfFragmentExists(POKEMON_LIST_FRAGMENT_TAG) && !isDeviceTablet) {
            loadFragment(PokemonListFragment.newInstance(false), POKEMON_LIST_FRAGMENT_TAG);
        }

        if (isDeviceTablet) {
            if (isDeviceTablet && flContainerContent != null && currentOrientation == ORIENTATION_LANDSCAPE) {
                loadFragment(AddPokemonFragment.newInstance(true), ADD_POKEMON_FRAGMENT_TAG);
                ViewCompat.setElevation(flContainerMain, 7);
            }

            // Load initial fragment every time
            loadFragment(PokemonListFragment.newInstance(false), POKEMON_LIST_FRAGMENT_TAG);
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                    R.string.drawer_close);

            drawerToggle.syncState();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.action_add:
                            if (checkIfFragmentExists(POKEMON_DETAILS_FRAGMENT_TAG)) {
                                removeFragmentFromStack(POKEMON_DETAILS_FRAGMENT_TAG);
                            }

                            loadFragment(AddPokemonFragment.newInstance(isDeviceTablet), ADD_POKEMON_FRAGMENT_TAG);
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
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

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

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

        transaction.remove(manager.findFragmentByTag(tag));
        transaction.commit();

        manager.popBackStack();
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }

    private void setupDrawerContent() {
        navigationDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        return true;
                    }
                }
        );
    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;

        switch(item.getItemId()) {
            case R.id.menu_item_logout:
                break;
            default:
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fl_container_main, fragment).commit();

        item.setChecked(true);

        setTitle(item.getTitle());

        drawerLayout.closeDrawers();
    }

    @Override
    public void onAddPokemonClick() {
        if (checkIfFragmentExists(POKEMON_DETAILS_FRAGMENT_TAG)) {
            removeFragmentFromStack(POKEMON_DETAILS_FRAGMENT_TAG);
        }

        if (checkIfFragmentExists(ADD_POKEMON_FRAGMENT_TAG) && !isDeviceTablet) {
            removeFragmentFromStack(ADD_POKEMON_FRAGMENT_TAG);
        }

        loadFragment(AddPokemonFragment.newInstance(isDeviceTablet), ADD_POKEMON_FRAGMENT_TAG);
    }

    @Override
    public void onShowPokemonDetailsClick(PokemonModel pokemon) {
        if (checkIfFragmentExists(POKEMON_DETAILS_FRAGMENT_TAG) && isDeviceTablet) {
            removeFragmentFromStack(POKEMON_DETAILS_FRAGMENT_TAG);
        }

        loadFragment(PokemonDetailsFragment.newInstance(pokemon, isDeviceTablet), POKEMON_DETAILS_FRAGMENT_TAG);
    }

    @Override
    public void onPokemonAdded(PokemonModel pokemon) {
        if (!isDeviceTablet) {
            removeFragmentFromStack(ADD_POKEMON_FRAGMENT_TAG);
        }
        else {
            if (currentOrientation == ORIENTATION_LANDSCAPE) {
                removeFragmentFromStack(POKEMON_LIST_FRAGMENT_TAG);
            }
        }

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
                if (isDeviceTablet && currentOrientation == ORIENTATION_LANDSCAPE) {
                    finish();
                }

                ((AddPokemonFragment) fragment).clearUserData();

                removeFragmentFromStack(ADD_POKEMON_FRAGMENT_TAG);
            }
        }
    }
}
