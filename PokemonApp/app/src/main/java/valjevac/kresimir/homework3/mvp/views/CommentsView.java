package valjevac.kresimir.homework3.mvp.views;

import java.util.ArrayList;

import valjevac.kresimir.homework3.models.Comment;

public interface CommentsView extends BaseView {

    void onCommentsLoadSuccess(ArrayList<Comment> comments, String currentPage);
}
