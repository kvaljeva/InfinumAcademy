package valjevac.kresimir.homework3.mvp.views;

public interface PokemonDetailsView extends BaseView {

    void onUpvoteSuccess();

    void onUpvoteFail();

    void onNewCommentSuccess();

    void onNewCommentFail();

    void onDownvoteSuccess();

    void onDownvoteFail();

    void onCommentsLoadSuccess();

    void onCommentsLoadFail();
}
