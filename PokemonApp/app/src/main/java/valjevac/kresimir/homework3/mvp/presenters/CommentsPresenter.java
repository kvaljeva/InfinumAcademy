package valjevac.kresimir.homework3.mvp.presenters;

public interface CommentsPresenter {

    void loadComments();

    void deleteComment(int commentId, int position);

    void cancel();
}
