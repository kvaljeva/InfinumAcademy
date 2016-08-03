
package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
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
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.models.Pokemon;

public class PokemonDetailsFragment extends Fragment {
    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    private static final String POKEMON_DETAILS = "PokemonDetails";

    private static final String DECIMAL_FORMAT = "0.00";

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
        return new PokemonDetailsFragment();
    }

    public static PokemonDetailsFragment newInstance(Pokemon pokemon, boolean isDeviceTablet) {
        PokemonDetailsFragment fragment = new PokemonDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(POKEMON_DETAILS, pokemon);
        fragment.setArguments(bundle);

        return fragment;
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
                DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT);
                double weight = pokemon.getWeight();
                double height = pokemon.getHeight();

                String heightFixed = transformHeightString(decimalFormat.format(height));
                String weightFixed = decimalFormat.format(weight) + R.string.weight_unit;
                String gender = (pokemon.getGender() == 1) ? getString(R.string.gender_male) : getString(R.string.gender_female);

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
        final MainActivity mainActivity = (MainActivity) getActivity();

        if (toolbar != null) {
            mainActivity.setSupportActionBar(toolbar);

            toolbar.setTitle(R.string.add_pokemon_toolbar_title);

            if (mainActivity.getSupportActionBar() != null) {
                mainActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.enter_right);
        }
        else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.exit_right);
        }
    }
}
