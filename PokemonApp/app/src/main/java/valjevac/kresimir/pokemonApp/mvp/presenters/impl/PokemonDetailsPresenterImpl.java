package valjevac.kresimir.pokemonApp.mvp.presenters.impl;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.helpers.ApiErrorHelper;
import valjevac.kresimir.pokemonApp.helpers.NetworkHelper;
import valjevac.kresimir.pokemonApp.helpers.SharedPreferencesHelper;
import valjevac.kresimir.pokemonApp.interfaces.AddCommentListener;
import valjevac.kresimir.pokemonApp.interfaces.CommentLoadListener;
import valjevac.kresimir.pokemonApp.interfaces.DeleteCommentListener;
import valjevac.kresimir.pokemonApp.interfaces.DownvoteListener;
import valjevac.kresimir.pokemonApp.interfaces.UpvoteListener;
import valjevac.kresimir.pokemonApp.models.AuthorData;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.Comment;
import valjevac.kresimir.pokemonApp.models.ExtendedData;
import valjevac.kresimir.pokemonApp.models.Links;
import valjevac.kresimir.pokemonApp.models.Pokemon;
import valjevac.kresimir.pokemonApp.models.User;
import valjevac.kresimir.pokemonApp.mvp.interactors.impl.PokemonDetailsInteractorImpl;
import valjevac.kresimir.pokemonApp.mvp.presenters.PokemonDetailsPresenter;
import valjevac.kresimir.pokemonApp.mvp.views.PokemonDetailsView;

public class PokemonDetailsPresenterImpl implements PokemonDetailsPresenter {

    private PokemonDetailsView view;

    private ArrayList<Comment> comments;

    private PokemonDetailsInteractorImpl interactor;

    private Pokemon pokemon;

    private Links links;

    private static final int COMMENT_AUTHOR = 0;

    private static final String GENDER_UNKNOWN = "Unknown";

    private static final String DELETE_FORBIDDEN = "forbidden";

    public PokemonDetailsPresenterImpl(PokemonDetailsView view, ArrayList<Comment> comments, Pokemon pokemon) {
        this.view = view;
        this.comments = comments;
        this.pokemon = pokemon;

        interactor = new PokemonDetailsInteractorImpl();
    }

    @Override
    public void handlePokemonData(String emptyValue, String weightUnit) {
        String heightFixed = transformHeightString(Double.toString(round(pokemon.getHeight(), 2)));
        String weightFixed = Double.toString(round(pokemon.getWeight(), 2)) + weightUnit;
        String gender = (pokemon.getGender().equals(GENDER_UNKNOWN)) ? emptyValue : pokemon.getGender().substring(0, 1);

        String moves = (!TextUtils.isEmpty(pokemon.getMoves())) ? pokemon.getMoves() : emptyValue;
        String types = (!TextUtils.isEmpty(pokemon.getTypes())) ? pokemon.getTypes() : emptyValue;

        view.onPokemonDataHandled(heightFixed, weightFixed, gender, moves, types);
    }

    @Override
    public void getComments() {

        if (!checkIfNetworkAvailable()) {
            return;
        }

        comments.clear();

        view.showProgress();

        interactor.getComments(pokemon.getId(), new CommentLoadListener() {
            @Override
            public void onCommentsLoadSuccess(BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>> body) {
                ArrayList<BaseData<User>> includedList = body.getIncluded();

                // Links for the all comments fragment
                links = body.getLinks();

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
                    comments.add(comment);
                }

                view.onCommentsLoadSuccess(comments);
                view.hideProgress();
            }

            @Override
            public void onCommentsLoadFail(String error) {

                if (ApiErrorHelper.createError(error)) {
                    view.showMessage(ApiErrorHelper.getFullError(0));
                }

                view.hideProgress();
            }
        });
    }

    @Override
    public void upvote() {

        if (!checkIfNetworkAvailable() || pokemon.getVote() == 1) {
            return;
        }

        view.showProgressDialog();

        interactor.upvote(pokemon.getId(), new UpvoteListener() {
            @Override
            public void onUpvoteSuccess() {
                view.hideProgressDialog();
                view.onUpvoteSuccess();
            }

            @Override
            public void onUpvoteFail(String error) {
                view.showMessage(error);
                view.hideProgressDialog();
                view.onUpvoteFail();
            }
        });
    }

    @Override
    public void downvote() {

        if (!checkIfNetworkAvailable() || pokemon.getVote() == -1) {
            return;
        }

        view.showProgressDialog();

        interactor.downvote(pokemon.getId(), new DownvoteListener() {
            @Override
            public void onDownvoteSuccess() {
                view.hideProgressDialog();
                view.onDownvoteSuccess();
            }

            @Override
            public void onDownvoteFail(String error) {
                view.showMessage(error);
                view.hideProgressDialog();
                view.onDownvoteFail();
            }
        });
    }

    @Override
    public void addComment(String commentBody) {

        if (!checkIfNetworkAvailable()) {
            return;
        }

        if (TextUtils.isEmpty(commentBody)) {
            view.showMessage(R.string.empty_coment_error);
            return;
        }

        view.showProgressDialog();

        final Comment comment = new Comment(commentBody);
        BaseData<Comment> data = new BaseData<>(comment);
        BaseResponse<BaseData<Comment>> request = new BaseResponse<>(data);

        interactor.addComment(pokemon.getId(), request, new AddCommentListener() {
            @Override
            public void onAddCommentSuccess(BaseResponse<BaseData<Comment>> body) {
                view.hideProgressDialog();

                Comment comment = body.getData().getAttributes();
                String username = body.getIncluded().get(COMMENT_AUTHOR).getAttributes().getUsername();

                comment.setId(body.getData().getId());
                comment.setUsername(username);

                comments.add(comment);

                view.onAddCommentSuccess(comments);
            }

            @Override
            public void onAddCommentFail(String error) {
                view.hideProgressDialog();

                if (ApiErrorHelper.createError(error)) {
                    view.showMessage(ApiErrorHelper.getFullError(0));
                }
            }
        });
    }

    @Override
    public void deleteComment(int commentId, final int position) {

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return;
        }

        if (!isUserCommentOwner(position)) {
            view.showMessage(R.string.comment_not_owner);
            return;
        }

        view.showProgressDialog();

        interactor.deleteComment(pokemon.getId(), commentId, new DeleteCommentListener() {
            @Override
            public void onDeleteCommentSuccess() {
                view.hideProgressDialog();

                comments.remove(position);

                view.onCommentDeleted(comments);
                view.showMessage(R.string.delete_success);
            }

            @Override
            public void onDeleteCommentFail(String error) {
                view.hideProgressDialog();

                if (ApiErrorHelper.createError(error)) {

                    if (ApiErrorHelper.getErrorAt(0).getDetail().equals(DELETE_FORBIDDEN)) {
                        view.showMessage(R.string.comment_not_owner);
                    }
                    else {
                        view.showMessage(R.string.delete_fail);
                    }
                }
            }
        });
    }

    @Override
    public void cancel() {

        if (interactor != null) {
            interactor.cancel();
        }
    }

    @Override
    public void showAllComments() {
        view.onShowAllComments(pokemon, comments, links);
    }

    public boolean checkIfNetworkAvailable() {

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return false;
        }

        return true;
    }

    private String transformHeightString(String height) {
        height = height.replace(".", "´ ");
        height += "˝";

        return height;
    }

    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);

        return bigDecimal.doubleValue();
    }

    private boolean isUserCommentOwner(int position) {
        String username = SharedPreferencesHelper.getString(SharedPreferencesHelper.USER);
        return comments.get(position).getUsername().equals(username);
    }
}
