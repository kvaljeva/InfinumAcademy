package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.PokemonListActivity;
import valjevac.kresimir.homework3.adapters.PokemonAdapter;
import valjevac.kresimir.homework3.listeners.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.PokemonModel;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class PokemonListFragment extends Fragment {
    private Unbinder unbinder;
    private OnFragmentInteractionListener listener;

    private static PokemonListFragment instance;

    public static final String POKEMON = "Pokemon";

    public static final String POKEMON_LIST_SATE = "PokemonListState";

    public static final String EMPTY_STATE = "EmptyState";

    public static final String LOAD_ANIMATION = "LoadAnimation";

    private ArrayList<PokemonModel> pokemonList;

    private PokemonAdapter pokemonAdapter;

    private static boolean animate;

    boolean isEmptyState;

    @BindView(R.id.recycler_view_pokemon_list)
    RecyclerView rvPokemonList;

    @BindView(R.id.ll_empty_state_container)
    LinearLayout llEmptyStateContainer;

    @BindView(R.id.ll_list_items_container)
    LinearLayout llItemsContainer;

    @Nullable
    @BindView(R.id.tb_pokemon_list)
    Toolbar toolbar;

    Call<BaseResponse<ArrayList<Data<PokemonModel>>>> pokemonListCall;

    public PokemonListFragment() { }

    public interface OnFragmentInteractionListener {

        void onAddPokemonClick();

        void onShowPokemonDetailsClick(PokemonModel pokemon);

        void onPokemonListLoaded();
    }

    public static PokemonListFragment newInstance(boolean loadAnimation) {

        PokemonListFragment fragment = new PokemonListFragment();

        Bundle args = new Bundle();
        args.putBoolean(LOAD_ANIMATION, loadAnimation);
        fragment.setArguments(args);

        return fragment;
    }

    public static PokemonListFragment newInstance(PokemonModel pokemon) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(POKEMON, pokemon);

        if (instance == null) {
            instance = new PokemonListFragment();
            instance.getArguments().putAll(bundle);

            return instance;
        }

        instance.getArguments().putAll(bundle);

        updateListState();

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pokemon_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        isEmptyState = true;

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

        pokemonAdapter = new PokemonAdapter(getActivity(), pokemonList, new RecyclerViewClickListener<PokemonModel>() {
            @Override
            public void OnClick(PokemonModel pokemon) {
                listener.onShowPokemonDetailsClick(pokemon);
            }
        });

        rvPokemonList.setAdapter(pokemonAdapter);
        rvPokemonList.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (pokemonList != null && pokemonList.size() > 0 && isEmptyState) {
            isEmptyState = false;
        }

        updatePokemonListOverview();

        return view;
    }

    private void fetchPokemonList() {
        pokemonListCall = ApiManager.getService().getPokemons();

        pokemonListCall.enqueue(new BaseCallback<BaseResponse<ArrayList<Data<PokemonModel>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                Log.e("API_POKEMON_LIST", error);
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<Data<PokemonModel>>> body, Response<BaseResponse<ArrayList<Data<PokemonModel>>>> response) {

                for (Data data : body.getData()) {
                    if (data.getAttributes() instanceof PokemonModel) {

                        pokemonList.add((PokemonModel) data.getAttributes());
                    }
                }

                if (pokemonList.size() > 0) {
                    isEmptyState = false;
                }

                listener.onPokemonListLoaded();

                updatePokemonListOverview();
            }
        });
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        if (toolbar != null) {
            PokemonListActivity pokemonListActivity = (PokemonListActivity) getActivity();

            pokemonListActivity.setSupportActionBar(toolbar);

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
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (pokemonListCall != null) {
            pokemonListCall.cancel();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_item_add, menu);
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
        if (animate) {
            if (enter) {
                return AnimationUtils.loadAnimation(getActivity(), R.anim.enter_left);
            }
            else {
                return AnimationUtils.loadAnimation(getActivity(), R.anim.exit_left);
            }
        }

        return new Animation() { };
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

    private static void updateListState() {
        Bundle arguments = instance.getArguments();

        if (arguments != null) {
            PokemonModel pokemon = arguments.getParcelable(POKEMON);

            if (pokemon != null) {
                instance.pokemonList.add(pokemon);
            }
        }
    }
}