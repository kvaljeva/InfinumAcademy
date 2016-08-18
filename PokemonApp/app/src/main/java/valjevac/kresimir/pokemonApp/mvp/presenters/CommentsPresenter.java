package valjevac.kresimir.pokemonApp.mvp.presenters;

public interface CommentsPresenter {

    void loadComments();

    void deleteComment(int commentId, int position);

    void cancel();
}
