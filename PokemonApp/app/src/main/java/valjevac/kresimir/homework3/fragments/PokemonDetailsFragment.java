
package valjevac.kresimir.homework3.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.BuildConfig;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.helpers.ApiErrorHelper;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Comment;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class PokemonDetailsFragment extends Fragment {
    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    private static final String POKEMON_DETAILS = "PokemonDetails";

    @BindView(R.id.tv_details_pokemon_name)
    TextView tvName;

    @BindView(R.id.tv_details_pokemon_desc)
    TextView tvDescription;

    @BindView(R.id.tv_pokemon_height_value)
    TextView tvHeight;

    @BindView(R.id.tv_pokemon_weight_value)
    TextView tvWeight;

    @BindView(R.id.tv_pokemon_type_value)
    TextView tvTypes;

    @BindView(R.id.tv_pokemon_moves_value)
    TextView tvMoves;

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

    @BindView(R.id.btn_like)
    ImageButton btnLike;

    @BindView(R.id.btn_dislike)
    ImageButton btnDislike;

    @BindView(R.id.et_comment_body)
    EditText etCommentBody;

    private ArrayList<Comment> commentList;

    private Pokemon pokemon;

    ProgressDialog progressDialog;

    Call<BaseResponse<Data<Pokemon>>> upvotePokemonCall;

    Call<BaseResponse<Data<Comment>>> createCommentCall;

    Call<BaseResponse<ArrayList<Data<Comment>>>> getCommentsCall;

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

        commentList = new ArrayList<>();

        setUpToolbar();

        Bundle arguments = getArguments();
        if (arguments != null) {
            pokemon = arguments.getParcelable(POKEMON_DETAILS);

            if (pokemon != null) {
                String heightFixed = transformHeightString(Double.toString(round(pokemon.getHeight(), 2)));
                String weightFixed = Double.toString(round(pokemon.getWeight(), 2)) + getString(R.string.weight_unit);
                String gender = (pokemon.getGender().equals("Unknown")) ? "N/A" : pokemon.getGender().substring(0, 1);

                String moves = (pokemon.getMoves() != null) ? pokemon.getMoves() : "N/A";
                String types = (pokemon.getType() != null) ? pokemon.getType() : "N/A";

                tvName.setText(pokemon.getName());
                tvDescription.setText(pokemon.getDescription());
                tvHeight.setText(heightFixed);
                tvWeight.setText(weightFixed);
                tvMoves.setText(moves);
                tvTypes.setText(types);
                tvGender.setText(gender);

                BitmapHelper.loadBitmap(ivImage, pokemon.getImage(), false);

                setButtonState(pokemon.getVote());
            }
        }

        fetchCommentList();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (upvotePokemonCall != null) {
            upvotePokemonCall.cancel();
        }

        if (createCommentCall != null) {
            createCommentCall.cancel();
        }

        if (getCommentsCall != null) {
            getCommentsCall.cancel();
        }
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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

    @OnClick(R.id.btn_like)
    public void likePokemon() {

        sendUpvoteRequest();

        setButtonState(1);
    }

    @OnClick(R.id.btn_dislike)
    public void dislikePokemon() {

        sendUpvoteRequest();

        setButtonState(-1);
    }

    public boolean checkIfNetworkAvailable() {

        if (!NetworkHelper.isNetworkAvailable()) {
            Toast.makeText(getActivity(), R.string.no_internet_conn, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void fetchCommentList() {

        if (!checkIfNetworkAvailable()) {
            return;
        }

        getCommentsCall = ApiManager.getService().getComments(pokemon.getId());
        getCommentsCall.enqueue(new BaseCallback<BaseResponse<ArrayList<Data<Comment>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                if (ApiErrorHelper.createError(error)) {
                    Toast.makeText(getActivity(), ApiErrorHelper.getFullError(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<Data<Comment>>> body, Response<BaseResponse<ArrayList<Data<Comment>>>> response) {

                for (Data data : body.getData()) {
                    commentList.add((Comment) data.getAttributes());
                }
            }
        });
    }

    @OnClick(R.id.btn_create_comment)
    public void createComment() {

        String commentBody = etCommentBody.getText().toString();

        if (TextUtils.isEmpty(commentBody)) {
            Toast.makeText(getActivity(), R.string.empty_coment_error, Toast.LENGTH_SHORT).show();
            return;
        }

        sendComment(commentBody);
    }

    private void sendComment(String commentBody) {

        if (!checkIfNetworkAvailable()) {
            return;
        }

        showProgressDialog(true);

        Comment comment = new Comment(commentBody);
        Data<Comment> data = new Data<>(comment);
        BaseResponse<Data<Comment>> request = new BaseResponse<>(data);

        createCommentCall = ApiManager.getService().insertComment(pokemon.getId(), request);
        createCommentCall.enqueue(new BaseCallback<BaseResponse<Data<Comment>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                showProgressDialog(false);

                if (ApiErrorHelper.createError(error)) {
                    Toast.makeText(getActivity(), ApiErrorHelper.getFullError(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(BaseResponse<Data<Comment>> body, Response<BaseResponse<Data<Comment>>> response) {
                showProgressDialog(false);

                commentList.add(body.getData().getAttributes());
            }
        });
    }

    private void sendUpvoteRequest() {

        if (!checkIfNetworkAvailable()) {
            return;
        }

        showProgressDialog(true);

        upvotePokemonCall = ApiManager.getService().votePokemon(pokemon.getId());
        upvotePokemonCall.enqueue(new BaseCallback<BaseResponse<Data<Pokemon>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                showProgressDialog(false);
            }

            @Override
            public void onSuccess(BaseResponse<Data<Pokemon>> body, Response<BaseResponse<Data<Pokemon>>> response) {

                showProgressDialog(false);
            }
        });
    }

    private void setButtonState(int state) {

        switch (state) {
            case -1:
                btnDislike.setImageResource(R.drawable.ic_dislike_on);
                btnLike.setImageResource(R.drawable.ic_like_off);
                break;
            case 0:
                btnDislike.setImageResource(R.drawable.ic_dislike_off);
                btnLike.setImageResource(R.drawable.ic_like_off);
                break;
            case 1:
                btnDislike.setImageResource(R.drawable.ic_dislike_off);
                btnLike.setImageResource(R.drawable.ic_like_on);
                break;
        }
    }

    private void showProgressDialog(boolean showProgress) {
        if (showProgress) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Hang on a sec...");

            progressDialog.show();
        }
        else {
            progressDialog.dismiss();
        }
    }
}
