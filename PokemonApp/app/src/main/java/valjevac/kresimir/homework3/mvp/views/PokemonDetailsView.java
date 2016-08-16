package valjevac.kresimir.homework3.mvp.views;

import java.util.ArrayList;

import valjevac.kresimir.homework3.models.Comment;
import valjevac.kresimir.homework3.models.Links;
import valjevac.kresimir.homework3.models.Pokemon;

public interface PokemonDetailsView extends BaseView {

    void onUpvoteSuccess();

    void onUpvoteFail();

    void onAddCommentSuccess(ArrayList<Comment> comments);

    void onDownvoteSuccess();

    void onDownvoteFail();

    void onCommentsLoadSuccess(ArrayList<Comment> comments);

    void showProgressDialog();

    void hideProgressDialog();

    void onShowAllComments(Pokemon pokemon, ArrayList<Comment> comments, Links links);

    void onPokemonDataHandled(String height, String weight, String gender, String moves, String types);
}
