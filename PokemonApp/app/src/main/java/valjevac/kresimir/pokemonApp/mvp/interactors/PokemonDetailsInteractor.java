package valjevac.kresimir.pokemonApp.mvp.interactors;

import valjevac.kresimir.pokemonApp.interfaces.AddCommentListener;
import valjevac.kresimir.pokemonApp.interfaces.CommentLoadListener;
import valjevac.kresimir.pokemonApp.interfaces.DeleteCommentListener;
import valjevac.kresimir.pokemonApp.interfaces.DownvoteListener;
import valjevac.kresimir.pokemonApp.interfaces.UpvoteListener;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.Comment;

public interface PokemonDetailsInteractor {

    void addComment(int pokemonId, BaseResponse<BaseData<Comment>> request, AddCommentListener listener);

    void deleteComment(int pokemonId, int commentId, final DeleteCommentListener listener);

    void upvote(int pokemonId, UpvoteListener listener);

    void downvote(int pokemonId, DownvoteListener listener);

    void getComments(int pokemonId, CommentLoadListener listener);

    void cancel();
}
