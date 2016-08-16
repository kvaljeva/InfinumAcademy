package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import valjevac.kresimir.homework3.ProcessPokemonList;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.adapters.PokemonAdapter;
import valjevac.kresimir.homework3.interfaces.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.mvp.presenters.impl.PokemonListPresenterImpl;
import valjevac.kresimir.homework3.mvp.views.PokemonListView;

public class PokemonListFragment extends Fragment implements PokemonListView, ProcessPokemonList.OnProcessingFinishedListener {

    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    public static final String POKEMON_LIST_SATE = "PokemonListState";

    public static final String LOAD_ANIMATION = "LoadAnimation";

    public static final String POKEMON = "Pokemon";

    private ArrayList<Pokemon> pokemonList;

    private PokemonAdapter pokemonAdapter;

    private boolean isEmptyState;

    private Snackbar snackbarProgress;

    private PokemonListPresenterImpl presenter;

    boolean animate;

    ActionBarDrawerToggle drawerToggle;

    @BindView(R.id.recycler_view_pokemon_list)
    RecyclerView rvPokemonList;

    @BindView(R.id.ll_empty_state_container)
    LinearLayout llEmptyStateContainer;

    @BindView(R.id.srl_recycler_container)
    SwipeRefreshLayout srlRecyclerContainer;

    @BindView(R.id.rl_main_container)
    RelativeLayout rlMainContainer;

    @Nullable
    @BindView(R.id.tb_pokemon_list)
    Toolbar toolbar;

    @BindView(R.id.nvDrawer)
    NavigationView navigationDrawer;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    public PokemonListFragment() { }

    public interface OnFragmentInteractionListener {

        void onAddPokemonClick();

        void onShowPokemonDetailsClick(Pokemon pokemon, ImageView imageView);

        void onLogoutClick();
    }

    public static PokemonListFragment newInstance(boolean loadAnimation) {

        PokemonListFragment fragment = new PokemonListFragment();

        Bundle args = new Bundle();
        args.putBoolean(LOAD_ANIMATION, loadAnimation);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            pokemonList = savedInstanceState.getParcelableArrayList(POKEMON_LIST_SATE);
        }
        else {
            pokemonList = new ArrayList<>();
        }

        presenter = new PokemonListPresenterImpl(this, pokemonList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_pokemon_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpToolbar();
        setUpRefreshView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        Pokemon pokemon = null;

        if (arguments != null) {
            animate = arguments.getBoolean(LOAD_ANIMATION);
            pokemon = arguments.getParcelable(POKEMON);

            arguments.clear();
        }

        if (pokemonList.size() == 0) {
            fetchPokemonList(true, getActivity().getString(R.string.loading_pokemon_list));
        }
        else {
            presenter.updatePokemonList(pokemon);
        }

        pokemonAdapter = new PokemonAdapter(getActivity(), pokemonList, new RecyclerViewClickListener<Pokemon>() {
            @Override
            public void OnClick(Pokemon pokemon, ImageView imageView) {
                listener.onShowPokemonDetailsClick(pokemon, imageView);
            }
        });

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(pokemonAdapter);
        alphaAdapter.setDuration(1000);

        SlideInBottomAnimationAdapter slideAdapter = new SlideInBottomAnimationAdapter(alphaAdapter);
        slideAdapter.setDuration(1000);
        slideAdapter.setInterpolator(new OvershootInterpolator(.5f));

        rvPokemonList.setItemAnimator(new FadeInAnimator());
        rvPokemonList.setAdapter(slideAdapter);
        rvPokemonList.setLayoutManager(new LinearLayoutManager(getActivity()));

        isEmptyState = pokemonList == null || pokemonList.size() == 0;

        updatePokemonListOverview(pokemonList);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(POKEMON_LIST_SATE, pokemonList);
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


    @Override
    public void onPokemonListLoadSuccess(ArrayList<Pokemon> pokemonList) {
        isEmptyState = (pokemonList.size() == 0);

        if (listener != null) {
            showProgress(false, null);
        }

        updatePokemonListOverview(pokemonList);
    }

    @Override
    public void onListRefreshNeeded(ArrayList<Pokemon> pokemonList) {
        updatePokemonListOverview(pokemonList);
    }

    @Override
    public void onPokemonListLoadFail(ArrayList<Pokemon> cachedPokemonList) {
        pokemonAdapter.update(cachedPokemonList);

        srlRecyclerContainer.setRefreshing(false);
    }

    @Override
    public void onLogout() {
        listener.onLogoutClick();
    }

    @Override
    public void showProgress() {
        srlRecyclerContainer.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        srlRecyclerContainer.setRefreshing(false);
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
    public void onDrawerContentReady(String username, String email) {
        View header = navigationDrawer.getHeaderView(0);

        TextView tvStudentMail = (TextView) header.findViewById(R.id.tv_student_mail);
        TextView tvStudentName = (TextView) header.findViewById(R.id.tv_student_name);

        tvStudentName.setText(username);
        tvStudentMail.setText(email);
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            MainActivity activity = (MainActivity) getActivity();

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

                            listener.onAddPokemonClick();
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
        String defaultUsername = getString(R.string.nav_student_name);
        String defaultEmail = getString(R.string.nav_student_mail);

        presenter.getDrawerContent(defaultUsername, defaultEmail);

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
                presenter.logout();
                break;
            default:
                break;
        }

        drawerLayout.closeDrawers();
    }

    private void setUpRefreshView() {

        srlRecyclerContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPokemonList(false, null);
            }
        });

        srlRecyclerContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent,
                android.R.color.holo_red_light
        );
    }

    private void fetchPokemonList(boolean showProgress, String message) {
        if (showProgress) {
            showProgress(true, message);
        }

        presenter.getPokemonList(true);
    }

    private void updatePokemonListOverview(ArrayList<Pokemon> updatedList) {
        if (isEmptyState) {

            llEmptyStateContainer.setVisibility(View.VISIBLE);
            rvPokemonList.setVisibility(View.INVISIBLE);
        }
        else {
            if (llEmptyStateContainer != null && llEmptyStateContainer.getVisibility() == View.VISIBLE) {

                llEmptyStateContainer.setVisibility(View.GONE);
                rvPokemonList.setVisibility(View.VISIBLE);
            }

            pokemonAdapter.update(updatedList);
        }
    }

    private void showProgress(final boolean isVisible, final String message) {
        rlMainContainer.post(new Runnable() {
            @Override
            public void run() {
                if (isVisible) {
                    snackbarProgress = Snackbar.make(rlMainContainer, message, Snackbar.LENGTH_INDEFINITE);
                    snackbarProgress.show();
                }
                else {
                    if (snackbarProgress != null) {
                        snackbarProgress.dismiss();
                    }
                }
            }
        });
    }

    @Override
    public void onProcessingFinished(ArrayList<Pokemon> pokemons) {
        presenter.updatePokemonList(pokemons);
    }
}