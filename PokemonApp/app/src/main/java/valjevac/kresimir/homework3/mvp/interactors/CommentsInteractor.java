package valjevac.kresimir.homework3.mvp.interactors;

import valjevac.kresimir.homework3.interfaces.CommentLoadListener;

public interface CommentsInteractor {

    void loadComments(String page, CommentLoadListener listener);

    void cancel();
}
