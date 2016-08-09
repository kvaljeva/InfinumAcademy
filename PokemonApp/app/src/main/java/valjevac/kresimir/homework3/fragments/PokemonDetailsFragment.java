
package valjevac.kresimir.homework3.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.helpers.ApiErrorHelper;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.models.AuthorData;
import valjevac.kresimir.homework3.models.RelationshipType;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Comment;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.ExtendedData;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class PokemonDetailsFragment extends Fragment {
    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    private static final String POKEMON_DETAILS = "PokemonDetails";

    private static final String DATE_FORMAT = "MMM dd, yyyy";

    private static final String GENDER_UNKNOWN = "Unknown";

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

    @BindView(R.id.ll_comments_container)
    LinearLayout llCommentsContainer;

    @BindView(R.id.first_comment_container)
    View vFirstComment;

    @BindView(R.id.second_comment_container)
    View vSecondComment;

    @BindView(R.id.fl_show_comments_container)
    FrameLayout flShowCommentsContainer;

    @BindView(R.id.sv_details_body_container)
    NestedScrollView nsvDetailsBody;

    private ArrayList<Comment> commentList;

    private Pokemon pokemon;

    ProgressDialog progressDialog;

    Call<BaseResponse<BaseData<Pokemon>>> upvotePokemonCall;

    Call<BaseResponse<BaseData<Pokemon>>> downvotePokemonCall;

    Call<BaseResponse<BaseData<Comment>>> createCommentCall;

    Call<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> getCommentsCall;

    BaseCallback<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> getCommentsCallback;

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

        void onShowAllCommentsPressed(String title, ArrayList<Comment> comments);
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
                String gender = (pokemon.getGender().equals(GENDER_UNKNOWN)) ? getActivity().getString(R.string.not_available) : pokemon.getGender().substring(0, 1);

                String moves = (pokemon.getMoves() != null) ? pokemon.getMoves() : getActivity().getString(R.string.not_available);
                String types = (pokemon.getType() != null) ? pokemon.getType() : getActivity().getString(R.string.not_available);

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

        // Reset focus so that we're always on the top when the view gets created
        nsvDetailsBody.smoothScrollTo(0, 0);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (upvotePokemonCall != null) {
            upvotePokemonCall.cancel();
        }

        if (downvotePokemonCall != null) {
            downvotePokemonCall.cancel();
        }

        if (createCommentCall != null) {
            createCommentCall.cancel();
        }

        if (getCommentsCallback != null) {
            getCommentsCallback.cancel();
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
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);

        return bigDecimal.doubleValue();
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
        if (pokemon.getVote() != 1) {
            sendUpvoteRequest();

            setButtonState(1);
        }
    }

    @OnClick(R.id.btn_dislike)
    public void dislikePokemon() {
        if (pokemon.getVote() != -1) {
            sendDownvoteRequest();

            setButtonState(-1);
        }
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

        getCommentsCallback = new BaseCallback<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                if (ApiErrorHelper.createError(error)) {
                    Toast.makeText(getActivity(), ApiErrorHelper.getFullError(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>> body, Response<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> response) {

                ArrayList<BaseData<User>> includedList = body.getIncluded();
                ArrayList<Comment> visibleCommentsList = new ArrayList<>();

                for (ExtendedData data : body.getData()) {
                    AuthorData author = (AuthorData) data.getRelationships().getModel().getData();
                    Comment comment = (Comment) data.getAttributes();

                    String username = "";
                    for (BaseData<User> user : includedList) {

                        if (user.getId() == author.getId()) {
                            username = user.getAttributes().getUsername();
                        }
                    }

                    comment.setId(data.getId());
                    comment.setUsername(username);
                    commentList.add(comment);

                    if (visibleCommentsList.size() < 2) {
                        visibleCommentsList.add(comment);
                    }
                }

                if (visibleCommentsList.size() > 0) {
                    setCommenData(vFirstComment, 0);

                    if (visibleCommentsList.size() > 1) {
                        setCommenData(vSecondComment, 1);
                    }
                }

                if (commentList.size() > 2) {
                    flShowCommentsContainer.setVisibility(View.VISIBLE);
                }
            }
        };

        getCommentsCall = ApiManager.getService().getComments(pokemon.getId());
        getCommentsCall.enqueue(getCommentsCallback);
    }

    private void setCommenData(View commentView, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

        TextView tvUsername = (TextView) commentView.findViewById(R.id.tv_comment_username);
        TextView tvBody = (TextView) commentView.findViewById(R.id.tv_comment_body);
        TextView tvDate = (TextView) commentView.findViewById(R.id.tv_comment_date);

        tvUsername.setText(commentList.get(position).getUsername());
        tvBody.setText(commentList.get(position).getContent());
        tvDate.setText(dateFormat.format(commentList.get(position).getDate()));
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

    @OnClick(R.id.btn_show_comments)
    public void showAllComments() {
        listener.onShowAllCommentsPressed(pokemon.getName() ,commentList);
    }

    private void sendComment(String commentBody) {

        if (!checkIfNetworkAvailable()) {
            return;
        }

        showProgressDialog(true);

        Comment comment = new Comment(commentBody);
        BaseData<Comment> data = new BaseData<>(comment);
        BaseResponse<BaseData<Comment>> request = new BaseResponse<>(data);

        createCommentCall = ApiManager.getService().insertComment(pokemon.getId(), request);
        createCommentCall.enqueue(new BaseCallback<BaseResponse<BaseData<Comment>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                showProgressDialog(false);

                if (ApiErrorHelper.createError(error)) {
                    Toast.makeText(getActivity(), ApiErrorHelper.getFullError(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<Comment>> body, Response<BaseResponse<BaseData<Comment>>> response) {
                showProgressDialog(false);

                commentList.add(body.getData().getAttributes());
                etCommentBody.setText("");
            }
        });
    }

    private void sendDownvoteRequest() {

        if (!checkIfNetworkAvailable()) {
            return;
        }

        showProgressDialog(true);

        downvotePokemonCall = ApiManager.getService().downvotePokemon(pokemon.getId());
        downvotePokemonCall.enqueue(new BaseCallback<BaseResponse<BaseData<Pokemon>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                setButtonState(pokemon.getVote());
                showProgressDialog(false);

                if (ApiErrorHelper.createError(error)) {
                    Toast.makeText(getActivity(), ApiErrorHelper.getFullError(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<Pokemon>> body, Response<BaseResponse<BaseData<Pokemon>>> response) {

                pokemon.setVote(-1);
                showProgressDialog(false);
            }
        });
    }

    private void sendUpvoteRequest() {

        if (!checkIfNetworkAvailable()) {
            return;
        }

        showProgressDialog(true);

        upvotePokemonCall = ApiManager.getService().votePokemon(pokemon.getId());
        upvotePokemonCall.enqueue(new BaseCallback<BaseResponse<BaseData<Pokemon>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                setButtonState(pokemon.getVote());
                showProgressDialog(false);

                if (ApiErrorHelper.createError(error)) {
                    Toast.makeText(getActivity(), ApiErrorHelper.getFullError(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<Pokemon>> body, Response<BaseResponse<BaseData<Pokemon>>> response) {

                pokemon.setVote(1);
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
            progressDialog.setMessage(getActivity().getString(R.string.progress_dialog_hang_on));

            progressDialog.show();
        }
        else {
            progressDialog.dismiss();
        }
    }
}
