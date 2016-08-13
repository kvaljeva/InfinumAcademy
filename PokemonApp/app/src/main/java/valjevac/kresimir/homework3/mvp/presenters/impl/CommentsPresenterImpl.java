package valjevac.kresimir.homework3.mvp.presenters.impl;

import android.text.TextUtils;

import java.util.ArrayList;

import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.helpers.ApiErrorHelper;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.interfaces.CommentLoadListener;
import valjevac.kresimir.homework3.models.AuthorData;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Comment;
import valjevac.kresimir.homework3.models.ExtendedData;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.mvp.interactors.impl.CommentsInteractorImpl;
import valjevac.kresimir.homework3.mvp.presenters.CommentsPresenter;
import valjevac.kresimir.homework3.mvp.views.CommentsView;

public class CommentsPresenterImpl implements CommentsPresenter {

    private CommentsView view;

    private CommentsInteractorImpl interactor;

    private ArrayList<Comment> commentList;

    private String nextPage;

    private static final int CURRENT_ERROR = 0;

    public CommentsPresenterImpl(CommentsView view, String page) {
        this.view = view;
        this.nextPage = page;

        interactor = new CommentsInteractorImpl();
        commentList = new ArrayList<>();
    }

    @Override
    public void loadComments() {

        if (TextUtils.isEmpty(nextPage)) {
            return;
        }

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return;
        }

        view.showProgress();

        interactor.loadComments(nextPage, new CommentLoadListener() {
            @Override
            public void onCommentsLoadSuccess(BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>> body) {

                ArrayList<BaseData<User>> includedList = body.getIncluded();

                for (ExtendedData data : body.getData()) {
                    AuthorData author = (AuthorData) data.getRelationships().getModel().getData();
                    Comment comment = (Comment) data.getAttributes();

                    String username = "";
                    for (BaseData<User> user : includedList) {

                        if (user.getId() == author.getId()) {
                            username = user.getAttributes().getUsername();
                        }
                    }

                    comment.setId(data.getId());
                    comment.setUsername(username);
                    commentList.add(comment);
                }

                view.onCommentsLoadSuccess(commentList);

                nextPage = (!TextUtils.isEmpty(body.getLinks().getNext())) ? body.getLinks().getNext() : "";

                view.hideProgress();
            }

            @Override
            public void onCommentsLoadFail(String error) {
                view.hideProgress();

                if (ApiErrorHelper.createError(error)) {
                    view.showMessage(ApiErrorHelper.getFullError(CURRENT_ERROR));
                }
            }
        });
    }

    @Override
    public void cancel() {

        if (interactor != null) {
            interactor.cancel();
        }
    }
}
