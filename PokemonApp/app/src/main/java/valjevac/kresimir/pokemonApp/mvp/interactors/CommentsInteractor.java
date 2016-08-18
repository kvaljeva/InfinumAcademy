package valjevac.kresimir.pokemonApp.mvp.interactors;

import valjevac.kresimir.pokemonApp.interfaces.CommentLoadListener;
import valjevac.kresimir.pokemonApp.interfaces.DeleteCommentListener;

public interface CommentsInteractor {

    void loadComments(String page, CommentLoadListener listener);

    void deleteComment(int pokemonId, int commentId, DeleteCommentListener listener);

    void cancel();
}
