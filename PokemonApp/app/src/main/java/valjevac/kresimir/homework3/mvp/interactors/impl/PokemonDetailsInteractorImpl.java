package valjevac.kresimir.homework3.mvp.interactors.impl;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.interfaces.AddCommentListener;
import valjevac.kresimir.homework3.interfaces.CommentLoadListener;
import valjevac.kresimir.homework3.interfaces.DeleteCommentListener;
import valjevac.kresimir.homework3.interfaces.DownvoteListener;
import valjevac.kresimir.homework3.interfaces.UpvoteListener;
import valjevac.kresimir.homework3.models.AuthorData;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Comment;
import valjevac.kresimir.homework3.models.ExtendedData;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.mvp.interactors.PokemonDetailsInteractor;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class PokemonDetailsInteractorImpl implements PokemonDetailsInteractor {

    Call<BaseResponse<BaseData<Pokemon>>> upvotePokemonCall;

    Call<BaseResponse<BaseData<Pokemon>>> downvotePokemonCall;

    Call<BaseResponse<BaseData<Comment>>> createCommentCall;

    Call<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> getCommentsCall;

    BaseCallback<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> getCommentsCallback;

    Call<Void> deleteCommentCall;

    public PokemonDetailsInteractorImpl() {
    }

    @Override
    public void addComment(int pokemonId, BaseResponse<BaseData<Comment>> request, final AddCommentListener listener) {
        createCommentCall = ApiManager.getService().insertComment(pokemonId, request);

        createCommentCall.enqueue(new BaseCallback<BaseResponse<BaseData<Comment>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onAddCommentFail(error);
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<Comment>> body, Response<BaseResponse<BaseData<Comment>>> response) {
                listener.onAddCommentSuccess(body);
            }
        });
    }

    @Override
    public void upvote(int pokemonId, final UpvoteListener listener) {
        upvotePokemonCall = ApiManager.getService().votePokemon(pokemonId);

        upvotePokemonCall.enqueue(new BaseCallback<BaseResponse<BaseData<Pokemon>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onUpvoteFail(error);
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<Pokemon>> body, Response<BaseResponse<BaseData<Pokemon>>> response) {
                listener.onUpvoteSuccess();
            }
        });
    }

    @Override
    public void downvote(int pokemonId, final DownvoteListener listener) {
        downvotePokemonCall = ApiManager.getService().downvotePokemon(pokemonId);

        downvotePokemonCall.enqueue(new BaseCallback<BaseResponse<BaseData<Pokemon>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onDownvoteFail(error);
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<Pokemon>> body, Response<BaseResponse<BaseData<Pokemon>>> response) {
                listener.onDownvoteSuccess();
            }
        });
    }

    @Override
    public void getComments(int pokemonId, final CommentLoadListener listener) {
        getCommentsCallback = new BaseCallback<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onCommentsLoadFail(error);
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>> body, Response<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> response) {
                listener.onCommentsLoadSuccess(body);
            }
        };

        getCommentsCall = ApiManager.getService().getComments(pokemonId);
        getCommentsCall.enqueue(getCommentsCallback);
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

        if (deleteCommentCall != null) {
            deleteCommentCall.cancel();
        }
    }
}
