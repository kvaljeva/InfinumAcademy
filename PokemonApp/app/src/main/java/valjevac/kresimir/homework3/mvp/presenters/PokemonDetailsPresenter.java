package valjevac.kresimir.homework3.mvp.presenters;

public interface PokemonDetailsPresenter {

    void handlePokemonData(String emptyValue, String weightUnit);

    void getComments();

    void deleteComment(int commentId, final int position);

    void upvote();

    void downvote();

    void addComment(String commentBody);

    void showAllComments();

    void cancel();
}
