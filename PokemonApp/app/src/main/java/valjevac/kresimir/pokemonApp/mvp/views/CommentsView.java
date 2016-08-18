package valjevac.kresimir.pokemonApp.mvp.views;

import java.util.ArrayList;

import valjevac.kresimir.pokemonApp.models.Comment;

public interface CommentsView extends BaseView {

    void onCommentsLoadSuccess(ArrayList<Comment> comments, String currentPage);

    void onCommentDeleted(int position);

    void showProgressDialog();

    void hideProgressDialog();
}
