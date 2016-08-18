package valjevac.kresimir.homework3.mvp.interactors;

import valjevac.kresimir.homework3.interfaces.CommentLoadListener;
import valjevac.kresimir.homework3.interfaces.DeleteCommentListener;

public interface CommentsInteractor {

    void loadComments(String page, CommentLoadListener listener);

    void deleteComment(int pokemonId, int commentId, DeleteCommentListener listener);

    void cancel();
}
