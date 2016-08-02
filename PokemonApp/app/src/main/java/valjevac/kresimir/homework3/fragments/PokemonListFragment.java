package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.ProcessPokemonList;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.PokemonListActivity;
import valjevac.kresimir.homework3.adapters.PokemonAdapter;
import valjevac.kresimir.homework3.database.SQLitePokemonList;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;
import valjevac.kresimir.homework3.interfaces.PokemonList;
import valjevac.kresimir.homework3.interfaces.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class PokemonListFragment extends Fragment implements ProcessPokemonList.OnProcessingFinishedListener {
    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    public static final String POKEMON_LIST_SATE = "PokemonListState";

    public static final String EMPTY_STATE = "EmptyState";

    public static final String LOAD_ANIMATION = "LoadAnimation";

    private ArrayList<Pokemon> pokemonList;

    private PokemonAdapter pokemonAdapter;

    private static boolean animate;

    private boolean isListLoading;

    boolean isEmptyState;

    @BindView(R.id.recycler_view_pokemon_list)
    RecyclerView rvPokemonList;

    @BindView(R.id.ll_empty_state_container)
    LinearLayout llEmptyStateContainer;

    @BindView(R.id.ll_list_items_container)
    LinearLayout llItemsContainer;

    @BindView(R.id.srl_recycler_container)
    SwipeRefreshLayout srlRecyclerContainer;

    @Nullable
    @BindView(R.id.tb_pokemon_list)
    Toolbar toolbar;

    @BindView(R.id.nvDrawer)
    NavigationView navigationDrawer;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    ActionBarDrawerToggle drawerToggle;

    Call<Void> logoutUserCall;

    Call<BaseResponse<ArrayList<Data<Pokemon>>>> pokemonListCall;

    PokemonList pokemonListDatabase;

    public PokemonListFragment() { }

    public interface OnFragmentInteractionListener {

        void onAddPokemonClick();

        void onShowPokemonDetailsClick(Pokemon pokemon);

        void onPokemonListLoad(boolean isSuccess);

        void onLogoutClick();
    }

    public static PokemonListFragment newInstance(boolean loadAnimation) {

        PokemonListFragment fragment = new PokemonListFragment();

        Bundle args = new Bundle();
        args.putBoolean(LOAD_ANIMATION, loadAnimation);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_pokemon_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        isEmptyState = true;

        pokemonListDatabase = new SQLitePokemonList();

        if (getArguments() != null) {
            Bundle args = getArguments();

            animate = args.getBoolean(LOAD_ANIMATION);
        }

        if (savedInstanceState == null) {

            if (pokemonList == null) {
                pokemonList = new ArrayList<>();

                // Fetch initial list
                fetchPokemonList();
            }

        }
        else {
            pokemonList = savedInstanceState.getParcelableArrayList(POKEMON_LIST_SATE);
            isEmptyState = savedInstanceState.getBoolean(EMPTY_STATE);

            // In case that there is something in the save state but that the list isn't initialized, do it here
            if (pokemonList == null) {
                pokemonList = new ArrayList<>();
            }
        }

        pokemonAdapter = new PokemonAdapter(getActivity(), pokemonList, new RecyclerViewClickListener<Pokemon>() {
            @Override
            public void OnClick(Pokemon pokemon) {
                listener.onShowPokemonDetailsClick(pokemon);
            }
        });

        rvPokemonList.setAdapter(pokemonAdapter);
        rvPokemonList.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (pokemonList != null && pokemonList.size() > 0 && isEmptyState) {
            isEmptyState = false;
        }

        updatePokemonListOverview();

        setUpToolbar();
        setUpRefreshView();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_item_add, menu);
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

        if (pokemonListCall != null) {
            pokemonListCall.cancel();
        }

        if (logoutUserCall != null) {
            logoutUserCall.cancel();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(POKEMON_LIST_SATE, pokemonList);

        if (pokemonList != null && pokemonList.size() > 0) {
            outState.putBoolean(EMPTY_STATE, false);
        }
        else {
            outState.putBoolean(EMPTY_STATE, true);
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        }
        else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        }
    }


    private void setUpToolbar() {
        if (toolbar != null) {
            PokemonListActivity activity = (PokemonListActivity) getActivity();

            activity.setSupportActionBar(toolbar);

            drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open,
                    R.string.drawer_close);

            setupDrawerContent();

            if (activity.getSupportActionBar() != null) {

                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setHomeButtonEnabled(true);
            }

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.action_add:

                            if (!isListLoading) {

                                listener.onAddPokemonClick();
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

    private void setupDrawerContent() {
        View header = navigationDrawer.getHeaderView(0);
        TextView tvStudentMail = (TextView) header.findViewById(R.id.tv_student_mail);
        TextView tvStudentName = (TextView) header.findViewById(R.id.tv_student_name);

        if (tvStudentMail != null && tvStudentName != null) {
            String username = SharedPreferencesHelper.getString(SharedPreferencesHelper.USER);
            String email = SharedPreferencesHelper.getString(SharedPreferencesHelper.EMAIL);

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email)) {
                tvStudentName.setText(username);
                tvStudentMail.setText(email);
            }
            else {
                tvStudentName.setText(R.string.nav_student_name);
                tvStudentMail.setText(R.string.nav_student_mail);
            }
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
                listener.onLogoutClick();
            }

            @Override
            public void onSuccess(Void body, Response<Void> response) {
                listener.onLogoutClick();
            }
        });
    }

    private void setUpRefreshView() {

        srlRecyclerContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPokemonList();
            }
        });

        srlRecyclerContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent,
                android.R.color.holo_red_light
        );
    }

    private void loadCachedList() {

        pokemonList.addAll(pokemonListDatabase.getPokemons());

        if (pokemonList.size() > 0) {
            isEmptyState = false;
        }

        listener.onPokemonListLoad(true);

        if (srlRecyclerContainer != null && srlRecyclerContainer.isRefreshing()) {
            srlRecyclerContainer.setRefreshing(false);
        }

        isListLoading = false;
    }

    private void fetchPokemonList() {
        pokemonList.clear();

        isListLoading = true;

        if (!NetworkHelper.isNetworkAvailable()) {

            loadCachedList();
            return;
        }

        pokemonListCall = ApiManager.getService().getPokemons();

        pokemonListCall.enqueue(new BaseCallback<BaseResponse<ArrayList<Data<Pokemon>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                Log.e("API_POKEMON_LIST", error);

                listener.onPokemonListLoad(false);

                // If it fails to fetch the list from the API, load the currently cached one
                loadCachedList();
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<Data<Pokemon>>> body, Response<BaseResponse<ArrayList<Data<Pokemon>>>> response) {

                ProcessPokemonList pokemonListProcessor = new ProcessPokemonList(body, pokemonListDatabase, PokemonListFragment.this);
                Thread thread = new Thread(pokemonListProcessor);

                thread.start();
            }
        });
    }

    private void updatePokemonListOverview() {
        if (isEmptyState) {

            llEmptyStateContainer.setVisibility(View.VISIBLE);
            llItemsContainer.setVisibility(View.GONE);
            rvPokemonList.setVisibility(View.INVISIBLE);
        }
        else {
            if (llEmptyStateContainer != null && llEmptyStateContainer.getVisibility() == View.VISIBLE) {

                llEmptyStateContainer.setVisibility(View.GONE);
                llItemsContainer.setVisibility(View.VISIBLE);
                rvPokemonList.setVisibility(View.VISIBLE);
            }

            pokemonAdapter.update(pokemonList);
        }
    }

    @Override
    public void onProcessingFinished(final ArrayList<Pokemon> pokemons) {

        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {

                pokemonList.addAll(pokemons);

                if (pokemonList.size() > 0) {
                    isEmptyState = false;
                }

                if (listener != null) {
                    listener.onPokemonListLoad(true);
                }

                updatePokemonListOverview();

                if (srlRecyclerContainer != null && srlRecyclerContainer.isRefreshing()) {
                    srlRecyclerContainer.setRefreshing(false);
                }

                isListLoading = false;
            }
        });
    }
}