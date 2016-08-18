package valjevac.kresimir.homework3.mvp.interactors;

import valjevac.kresimir.homework3.interfaces.AddCommentListener;
import valjevac.kresimir.homework3.interfaces.CommentLoadListener;
import valjevac.kresimir.homework3.interfaces.DeleteCommentListener;
import valjevac.kresimir.homework3.interfaces.DownvoteListener;
import valjevac.kresimir.homework3.interfaces.UpvoteListener;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Comment;

public interface PokemonDetailsInteractor {

    void addComment(int pokemonId, BaseResponse<BaseData<Comment>> request, AddCommentListener listener);

    void deleteComment(int pokemonId, int commentId, final DeleteCommentListener listener);

    void upvote(int pokemonId, UpvoteListener listener);

    void downvote(int pokemonId, DownvoteListener listener);

    void getComments(int pokemonId, CommentLoadListener listener);

    void cancel();
}
