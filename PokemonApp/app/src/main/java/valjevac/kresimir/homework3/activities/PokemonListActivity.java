package valjevac.kresimir.homework3.activities;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.fragments.ConfirmationDialogFragment;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.fragments.AddPokemonFragment;
import valjevac.kresimir.homework3.fragments.PokemonDetailsFragment;
import valjevac.kresimir.homework3.fragments.PokemonListFragment;
import valjevac.kresimir.homework3.fragments.ProgressLoadFragment;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;
import valjevac.kresimir.homework3.models.PokemonModel;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

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

    Call<Void> logoutUserCall;

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

    @Nullable
    @BindView(R.id.tv_student_mail)
    TextView tvStudentMail;

    @Nullable
    @BindView(R.id.tv_student_name)
    TextView tvStudentName;

    private ActionBarDrawerToggle drawerToggle;

    private boolean isListLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        ButterKnife.bind(this);

        isListLoading = true;

        isDeviceTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        currentOrientation = getCurrentOrientation();

        if (!checkIfFragmentExists(POKEMON_LIST_FRAGMENT_TAG) && !isDeviceTablet) {
            loadFragment(PokemonListFragment.newInstance(false), POKEMON_LIST_FRAGMENT_TAG);

            loadFragment(ProgressLoadFragment.newInstance(getString(R.string.progress_load_description), PROGRESS_LOAD_TITLE), PROGRESS_LOAD_FRAGMENT_TAG);
        }

        if (isDeviceTablet) {
            if (isDeviceTablet && flContainerContent != null && currentOrientation == ORIENTATION_LANDSCAPE) {
                loadFragment(AddPokemonFragment.newInstance(true), ADD_POKEMON_FRAGMENT_TAG);
                ViewCompat.setElevation(flContainerMain, 7);
            }

            // Load initial fragment every time
            loadFragment(PokemonListFragment.newInstance(false), POKEMON_LIST_FRAGMENT_TAG);
        }

        setUpToolbar();
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                    R.string.drawer_close);

            setupDrawerContent();

            if (getSupportActionBar() != null) {

                if (!getSupportActionBar().isShowing()) {
                    getSupportActionBar().show();
                }

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

                            if (!isListLoading) {
                                loadFragment(AddPokemonFragment.newInstance(isDeviceTablet), ADD_POKEMON_FRAGMENT_TAG);
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            });

            drawerToggle.syncState();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (logoutUserCall != null) {
            logoutUserCall.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item_add, menu);

        return true;
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

        if (tag.equals(PROGRESS_LOAD_FRAGMENT_TAG)) {
            transaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_out);
        }

        transaction.remove(manager.findFragmentByTag(tag));
        transaction.commit();

        manager.popBackStack();

        if (drawerToggle != null) {

            setUpToolbar();
        }
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }

    private void setupDrawerContent() {
        if (tvStudentMail != null && tvStudentName != null) {
            String username = SharedPreferencesHelper.getString(SharedPreferencesHelper.USER);
            String email = SharedPreferencesHelper.getString(SharedPreferencesHelper.EMAIL);

            tvStudentName.setText(username);
            tvStudentMail.setText(email);
        }

        navigationDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                }
        );
    }

    private void selectDrawerItem(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menu_item_logout:
                handleLogout();
                break;
            default:
                break;
        }

        drawerLayout.closeDrawers();
    }

    private void handleLogout() {
        logoutUserCall = ApiManager.getService().logoutUser();

        logoutUserCall.enqueue(new BaseCallback<Void>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                Toast.makeText(PokemonListActivity.this, "Failed to log out.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Void body, Response<Void> response) {
                SharedPreferencesHelper.setInt(0, SharedPreferencesHelper.USER_ID);
                SharedPreferencesHelper.setString("", SharedPreferencesHelper.AUTH_TOKEN);
                SharedPreferencesHelper.setString("", SharedPreferencesHelper.USER);
                SharedPreferencesHelper.setString("", SharedPreferencesHelper.EMAIL);

                Intent intent = new Intent(PokemonListActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    @Override
    public void onAddPokemonClick() {
        if (checkIfFragmentExists(POKEMON_DETAILS_FRAGMENT_TAG)) {
            removeFragmentFromStack(POKEMON_DETAILS_FRAGMENT_TAG);
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
    public void onPokemonListLoaded() {
        if (checkIfFragmentExists(PROGRESS_LOAD_FRAGMENT_TAG)) {
            removeFragmentFromStack(PROGRESS_LOAD_FRAGMENT_TAG);
            isListLoading = false;
        }
    }
}
