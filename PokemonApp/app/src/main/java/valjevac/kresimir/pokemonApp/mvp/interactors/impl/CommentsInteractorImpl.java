package valjevac.kresimir.pokemonApp.mvp.interactors.impl;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.pokemonApp.interfaces.CommentLoadListener;
import valjevac.kresimir.pokemonApp.interfaces.DeleteCommentListener;
import valjevac.kresimir.pokemonApp.models.AuthorData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.Comment;
import valjevac.kresimir.pokemonApp.models.ExtendedData;
import valjevac.kresimir.pokemonApp.mvp.interactors.CommentsInteractor;
import valjevac.kresimir.pokemonApp.network.ApiManager;
import valjevac.kresimir.pokemonApp.network.BaseCallback;

public class CommentsInteractorImpl implements CommentsInteractor {

    Call<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> getCommentsPageCall;

    Call<Void> deleteCommentCall;

    public CommentsInteractorImpl() {
    }

    @Override
    public void loadComments(String page, final CommentLoadListener listener) {
        getCommentsPageCall = ApiManager.getService().getCommentsPage(page);

        getCommentsPageCall.enqueue(new BaseCallback<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onCommentsLoadFail(error);
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>> body,
                                  Response<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> response) {

                listener.onCommentsLoadSuccess(body);
            }
        });
    }

    @Override
    public void deleteComment(int pokemonId, int commentId, final DeleteCommentListener listener) {
        deleteCommentCall = ApiManager.getService().deleteComment(pokemonId, commentId);

        deleteCommentCall.enqueue(new BaseCallback<Void>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onDeleteCommentFail(error);
            }

            @Override
            public void onSuccess(Void body, Response<Void> response) {
                listener.onDeleteCommentSuccess();
            }
        });
    }

    @Override
    public void cancel() {

        if (getCommentsPageCall != null) {
            getCommentsPageCall.cancel();
        }

        if (deleteCommentCall != null) {
            deleteCommentCall.cancel();
        }
    }
}
