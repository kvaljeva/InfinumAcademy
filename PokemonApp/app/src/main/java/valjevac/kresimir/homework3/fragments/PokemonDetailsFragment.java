
package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.PokemonListActivity;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.models.Pokemon;

public class PokemonDetailsFragment extends Fragment {
    private Unbinder unbinder;
    private OnFragmentInteractionListener listener;

    private static PokemonDetailsFragment instance;
    private static final String POKEMON_DETAILS = "PokemonDetails";
    private boolean isTabletView;

    @BindView(R.id.tv_details_pokemon_name)
    TextView tvName;

    @BindView(R.id.tv_details_pokemon_desc)
    TextView tvDescription;

    @BindView(R.id.tv_pokemon_height_value)
    TextView tvHeight;

    @BindView(R.id.tv_pokemon_weight_value)
    TextView tvWeight;

    @BindView(R.id.tv_pokemon_category_value)
    TextView tvCategory;

    @BindView(R.id.tv_pokemon_abilities_value)
    TextView tvAbilities;

    @BindView(R.id.tv_pokemon_gender_value)
    TextView tvGender;

    @BindView(R.id.iv_pokemon_image)
    ImageView ivImage;

    @Nullable
    @BindView(R.id.tb_pokemon_details)
    Toolbar toolbar;

    @BindView(R.id.abl_header_pokemon_details)
    AppBarLayout ablPokemonDetails;

    @BindView(R.id.ctl_header_pokemon_details)
    CollapsingToolbarLayout ctlHeaderPokemonDetails;

    public PokemonDetailsFragment() { }

    public static PokemonDetailsFragment newInstance() {

        if (instance == null) {
            instance = new PokemonDetailsFragment();
            return instance;
        }

        return instance;
    }

    public static PokemonDetailsFragment newInstance(Pokemon pokemon) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(POKEMON_DETAILS, pokemon);

        if (instance == null) {
            instance = new PokemonDetailsFragment();
            instance.setArguments(bundle);
            return instance;
        }

        instance.setArguments(bundle);
        return instance;
    }

    public static PokemonDetailsFragment newInstance(Pokemon pokemon, boolean isDeviceTablet) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(POKEMON_DETAILS, pokemon);

        if (instance == null) {
            instance = new PokemonDetailsFragment();
            instance.setArguments(bundle);
            instance.isTabletView = isDeviceTablet;

            return instance;
        }

        instance.getArguments().putAll(bundle);
        instance.isTabletView = isDeviceTablet;

        return instance;
    }

    public interface OnFragmentInteractionListener {

        void onDetailsHomePressed();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pokemon_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpToolbar();

        Bundle arguments = getArguments();
        if (arguments != null) {
            Pokemon pokemon = arguments.getParcelable(POKEMON_DETAILS);

            if (pokemon != null) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                double weight = pokemon.getWeight();
                double height = pokemon.getHeight();

                String heightFixed = transformHeightString(decimalFormat.format(height));
                String weightFixed = decimalFormat.format(weight);
                String gender = (pokemon.getGender() == 1) ? "M" : "F";

                tvName.setText(pokemon.getName());
                tvDescription.setText(pokemon.getDescription());
                tvHeight.setText(heightFixed);
                tvWeight.setText(weightFixed);
                tvAbilities.setText(pokemon.getMoves());
                tvCategory.setText(pokemon.getType());
                tvGender.setText(gender);

                BitmapHelper.loadBitmap(ivImage, pokemon.getImage(), false);
            }
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener.");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_transform));
            setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
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

    private String transformHeightString(String height) {
        height = height.replace(".", "´ ");
        height += "˝";

        return height;
    }

    private void setToolbarTitle() {
        if (ctlHeaderPokemonDetails != null) {
            ctlHeaderPokemonDetails.setTitle(getString(R.string.pokemon_details_toolbar_title));
            ctlHeaderPokemonDetails.setExpandedTitleColor(ContextCompat.getColor(getActivity(),
                    android.R.color.transparent));
        }
    }

    private void setUpToolbar() {
        final PokemonListActivity pokemonListActivity = (PokemonListActivity) getActivity();

        if (toolbar != null) {
            pokemonListActivity.setSupportActionBar(toolbar);

            toolbar.setTitle(R.string.add_pokemon_toolbar_title);

            if (pokemonListActivity.getSupportActionBar() != null) {
                pokemonListActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                pokemonListActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            setToolbarTitle();

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDetailsHomePressed();
                }
            });
        }
    }
}
