
package valjevac.kresimir.pokemonApp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.activities.MainActivity;
import valjevac.kresimir.pokemonApp.custom.ProgressView;
import valjevac.kresimir.pokemonApp.helpers.BitmapHelper;
import valjevac.kresimir.pokemonApp.models.Comment;
import valjevac.kresimir.pokemonApp.models.Links;
import valjevac.kresimir.pokemonApp.models.Pokemon;
import valjevac.kresimir.pokemonApp.mvp.presenters.impl.PokemonDetailsPresenterImpl;
import valjevac.kresimir.pokemonApp.mvp.views.PokemonDetailsView;

public class PokemonDetailsFragment extends Fragment implements PokemonDetailsView {
    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    private PokemonDetailsPresenterImpl presenter;

    private static final String POKEMON_DETAILS = "PokemonDetails";

    private static final String DATE_FORMAT = "MMM dd, yyyy";

    public static final String SHARED_TRANSITION = "SharedTransition";

    private static final int ELEVATION = 15;

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

    @BindView(R.id.btn_show_comments)
    Button btnShowComments;

    @BindView(R.id.sv_details_body_container)
    NestedScrollView nsvDetailsBody;

    @BindView(R.id.pv_comments)
    ProgressView pvComments;

    @BindView(R.id.ll_comment_input_container)
    LinearLayout llCommentInputContainer;

    private String transitionName;

    private Pokemon pokemon;

    ArrayList<Comment> commentList;

    ProgressDialog progressDialog;

    public PokemonDetailsFragment() { }

    public static PokemonDetailsFragment newInstance() {
        return new PokemonDetailsFragment();
    }

    public static PokemonDetailsFragment newInstance(Pokemon pokemon, String transitionName) {
        PokemonDetailsFragment fragment = new PokemonDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(POKEMON_DETAILS, pokemon);
        bundle.putString(SHARED_TRANSITION, transitionName);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static PokemonDetailsFragment newInstance(Pokemon pokemon) {
        PokemonDetailsFragment fragment = new PokemonDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(POKEMON_DETAILS, pokemon);
        fragment.setArguments(bundle);

        return fragment;
    }

    public interface OnFragmentInteractionListener {

        void onDetailsHomePressed();

        void onShowAllCommentsPressed(String title, ArrayList<Comment> comments, String nextPage, int pokemonId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            pokemon = arguments.getParcelable(POKEMON_DETAILS);
            transitionName = arguments.getString(SHARED_TRANSITION);
        }

        commentList = new ArrayList<>();
        presenter = new PokemonDetailsPresenterImpl(this, commentList, pokemon);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pokemon_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpToolbar();

        if (pokemon != null) {
            presenter.handlePokemonData(getString(R.string.not_available), getString(R.string.weight_unit));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivImage.setTransitionName(transitionName);
        }

        presenter.getComments();

        // Reset focus so that we're always on the top when the view gets created
        nsvDetailsBody.smoothScrollTo(0, 0);

        ViewCompat.setElevation(llCommentInputContainer, ELEVATION);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (presenter != null) {
            presenter.cancel();
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

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.enter_right);
        }
        else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.exit_right);
        }
    }

    @Override
    public void onPokemonDataHandled(String height, String weight, String gender, String moves, String types) {
        tvName.setText(pokemon.getName());
        tvDescription.setText(pokemon.getDescription());
        tvHeight.setText(height);
        tvWeight.setText(weight);
        tvMoves.setText(moves);
        tvTypes.setText(types);
        tvGender.setText(gender);

        BitmapHelper.loadBitmap(ivImage, pokemon.getImage(), false);

        setButtonState(pokemon.getVote());
    }

    @Override
    public void onUpvoteSuccess() {
        pokemon.setVote(1);
    }

    @Override
    public void onUpvoteFail() {
        setButtonState(pokemon.getVote());
    }

    @Override
    public void onAddCommentSuccess(ArrayList<Comment> comments) {
        updateCommentsOverview(comments);

        etCommentBody.setText("");
        nsvDetailsBody.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onDownvoteSuccess() {
        pokemon.setVote(-1);
    }

    @Override
    public void onDownvoteFail() {
        setButtonState(pokemon.getVote());
    }

    @Override
    public void onCommentsLoadSuccess(ArrayList<Comment> comments) {
        updateCommentsOverview(comments);
    }

    @Override
    public void showProgress() {
        pvComments.show();
    }

    @Override
    public void hideProgress() {
        pvComments.hide();
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
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getActivity().getString(R.string.progress_dialog_hang_on));

        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void onShowAllComments(Pokemon pokemon, ArrayList<Comment> comments, Links links) {
        listener.onShowAllCommentsPressed(pokemon.getName(), comments, links.getNext(), pokemon.getId());
    }

    @OnClick(R.id.btn_like)
    public void onUpvoteClick() {
        presenter.upvote();
        setButtonState(1);
    }

    @OnClick(R.id.btn_dislike)
    public void onDownvoteClick() {
        presenter.downvote();
        setButtonState(-1);
    }

    @OnClick(R.id.btn_create_comment)
    public void onCreateCommentClick() {

        String commentBody = etCommentBody.getText().toString();

        presenter.addComment(commentBody);
    }

    @OnClick(R.id.btn_show_comments)
    public void onShowAllCommentsClick() {
        presenter.showAllComments();
    }

    @Override
    public void onCommentDeleted(ArrayList<Comment> comments) {
        if (comments.size() == 0) {
            vFirstComment.setVisibility(View.GONE);
        }
        else if (comments.size() == 1) {
            vSecondComment.setVisibility(View.GONE);
        }

        updateCommentsOverview(comments);
    }

    private void setCommentData(View commentView, final ArrayList<Comment> comments, final int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

        TextView tvUsername = (TextView) commentView.findViewById(R.id.tv_comment_username);
        TextView tvBody = (TextView) commentView.findViewById(R.id.tv_comment_body);
        TextView tvDate = (TextView) commentView.findViewById(R.id.tv_comment_date);

        tvUsername.setText(comments.get(position).getUsername());
        tvBody.setText(comments.get(position).getContent());
        tvDate.setText(dateFormat.format(comments.get(position).getDate()));

        commentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int fixedPosition = (comments.size() == 1) ? 0 : position;

                showDialog(comments.get(fixedPosition).getId(), fixedPosition);

                return true;
            }
        });
    }

    private void showDialog(final int commentId, final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        presenter.deleteComment(commentId, position);
                        break;

                    default: break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_comment);
        builder.setMessage(R.string.delete_comment_desc)
                .setPositiveButton(R.string.dialog_button_positive, dialogClickListener)
                .setNegativeButton(R.string.dialog_button_negative, dialogClickListener)
                .show();
    }

    private void updateCommentsOverview(ArrayList<Comment> comments) {
        if (comments.size() == 0 ) {
            llCommentsContainer.setVisibility(View.GONE);
        }
        else {
            llCommentsContainer.setVisibility(View.VISIBLE);

            setCommentData(vFirstComment, comments, 0);
            vFirstComment.setVisibility(View.VISIBLE);

            if (comments.size() > 1) {
                setCommentData(vSecondComment, comments, 1);
                vSecondComment.setVisibility(View.VISIBLE);
            }

            if (comments.size() > 2) {
                btnShowComments.setVisibility(View.VISIBLE);
            }
        }
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
}
