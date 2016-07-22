
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.PokemonListActivity;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.models.PokemonModel;

public class PokemonDetailsFragment extends Fragment {
    private Unbinder unbinder;
    private OnFragmentInteractionListener listener;

    private static final String TOOLBAR_TITLE = "Pokemon Details";

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

    @BindView(R.id.tb_pokemon_details)
    Toolbar toolbar;

    @BindView(R.id.abl_header_pokemon_details)
    AppBarLayout ablPokemonDetails;

    @BindView(R.id.ctl_header_pokemon_details)
    CollapsingToolbarLayout ctlHeaderPokemonDetails;

    public PokemonDetailsFragment() {
        this.setArguments(new Bundle());
    }

    public static PokemonDetailsFragment newInstance() {
        return new PokemonDetailsFragment();
    }

    public interface OnFragmentInteractionListener {

        void onDetailsHomePressed();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pokemon_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        setToolbarTitle();

        if (toolbar != null) {
            PokemonListActivity activity = (PokemonListActivity) getActivity();

            activity.setSupportActionBar(toolbar);

            toolbar.setTitle(TOOLBAR_TITLE);

            if (activity.getSupportActionBar() != null) {

                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDetailsHomePressed();
                }
            });
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            PokemonModel pokemon = arguments.getParcelable(PokemonListFragment.POKEMON);

            if (pokemon != null) {
                String height = transformHeightString(String.valueOf(pokemon.getHeight()));
                String weight = String.valueOf(pokemon.getWeight());

                tvName.setText(pokemon.getName());
                tvDescription.setText(pokemon.getDescription());
                tvHeight.setText(height);
                tvWeight.setText(weight);
                tvAbilities.setText(pokemon.getAbilites());
                tvCategory.setText(pokemon.getCategory());
                tvGender.setText(pokemon.getGender());

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

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.enter_right);
        }
        else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.exit_right);
        }
    }

    private void setToolbarTitle() {
        if (ctlHeaderPokemonDetails != null) {
            ctlHeaderPokemonDetails.setTitle(getActivity().getTitle());
            ctlHeaderPokemonDetails.setExpandedTitleColor(ContextCompat.getColor(getActivity(),
                    android.R.color.transparent));
        }
    }

    private String transformHeightString(String height) {
        height = height.replace(".", "´ ");
        height += "˝";

        return height;
    }
}
