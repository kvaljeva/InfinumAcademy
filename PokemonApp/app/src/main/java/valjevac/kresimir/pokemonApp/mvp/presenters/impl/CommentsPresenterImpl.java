package valjevac.kresimir.pokemonApp.mvp.presenters.impl;

import android.text.TextUtils;

import java.util.ArrayList;

import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.helpers.ApiErrorHelper;
import valjevac.kresimir.pokemonApp.helpers.NetworkHelper;
import valjevac.kresimir.pokemonApp.helpers.SharedPreferencesHelper;
import valjevac.kresimir.pokemonApp.interfaces.CommentLoadListener;
import valjevac.kresimir.pokemonApp.interfaces.DeleteCommentListener;
import valjevac.kresimir.pokemonApp.models.AuthorData;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.Comment;
import valjevac.kresimir.pokemonApp.models.ExtendedData;
import valjevac.kresimir.pokemonApp.models.User;
import valjevac.kresimir.pokemonApp.mvp.interactors.impl.CommentsInteractorImpl;
import valjevac.kresimir.pokemonApp.mvp.presenters.CommentsPresenter;
import valjevac.kresimir.pokemonApp.mvp.views.CommentsView;

public class CommentsPresenterImpl implements CommentsPresenter {

    private CommentsView view;

    private CommentsInteractorImpl interactor;

    private ArrayList<Comment> commentList;

    private String nextPage;

    private int pokemonId;

    private static final int CURRENT_ERROR = 0;

    private static final String DELETE_FORBIDDEN = "forbidden";

    public CommentsPresenterImpl(CommentsView view, String page, ArrayList<Comment> comments, int pokemonId) {
        this.view = view;
        this.nextPage = page;
        this.pokemonId = pokemonId;

        interactor = new CommentsInteractorImpl();
        commentList = comments;
    }

    @Override
    public void loadComments() {

        if (TextUtils.isEmpty(nextPage)) {
            return;
        }

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return;
        }

        view.showProgress();

        interactor.loadComments(nextPage, new CommentLoadListener() {
            @Override
            public void onCommentsLoadSuccess(BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>> body) {

                ArrayList<BaseData<User>> includedList = body.getIncluded();

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
                }

                nextPage = (!TextUtils.isEmpty(body.getLinks().getNext())) ? body.getLinks().getNext() : "";

                view.onCommentsLoadSuccess(commentList, nextPage);
                view.hideProgress();
            }

            @Override
            public void onCommentsLoadFail(String error) {
                view.hideProgress();

                if (ApiErrorHelper.createError(error)) {
                    view.showMessage(ApiErrorHelper.getFullError(CURRENT_ERROR));
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

        interactor.deleteComment(pokemonId, commentId, new DeleteCommentListener() {
            @Override
            public void onDeleteCommentSuccess() {
                commentList.remove(position);

                view.hideProgressDialog();
                view.onCommentDeleted(position);
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
                }            }
        });
    }

    @Override
    public void cancel() {

        if (interactor != null) {
            interactor.cancel();
        }
    }

    private boolean isUserCommentOwner(int position) {
        String username = SharedPreferencesHelper.getString(SharedPreferencesHelper.USER);
        return commentList.get(position).getUsername().equals(username);
    }
}