package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.adapters.CommentAdapter;
import valjevac.kresimir.homework3.models.Comment;
import valjevac.kresimir.homework3.mvp.presenters.CommentsPresenter;
import valjevac.kresimir.homework3.mvp.presenters.impl.CommentsPresenterImpl;
import valjevac.kresimir.homework3.mvp.views.CommentsView;

public class CommentsFragment extends Fragment implements CommentsView {

    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    private static final String TITLE = "Title";

    private static final String COMMENT_LIST = "CommentList";

    private static final String NEXT_PAGE = "CommentsNextPage";

    private static final int ELEVATION = 14;

    public interface OnFragmentInteractionListener {

        void onCommentsHomePressed();
    }

    @BindView(R.id.rv_all_comments)
    RecyclerView rvAllComments;

    @BindView(R.id.toolbar_comments)
    Toolbar toolbar;

    @BindView(R.id.ll_all_comments_progress_container)
    LinearLayout llProgressContainer;

    private ArrayList<Comment> commentList;

    private String nextPage;

    private String title;

    private CommentAdapter commentAdapter;

    private CommentsPresenter presenter;

    public CommentsFragment() {

    }

    public static CommentsFragment newInstance(String title, ArrayList<Comment> commentList, String nextPage) {
        CommentsFragment fragment = new CommentsFragment();

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putParcelableArrayList(COMMENT_LIST, commentList);
        args.putString(NEXT_PAGE, nextPage);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = getActivity().getString(R.string.comments_fragment_default_title);

        Bundle arguments = getArguments();
        if (arguments != null && savedInstanceState == null) {

            title = arguments.getString(TITLE) + " " + getActivity().getString(R.string.comments);
            commentList = arguments.getParcelableArrayList(COMMENT_LIST);
            nextPage = arguments.getString(NEXT_PAGE);

            arguments.clear();
        }
        else if (savedInstanceState != null) {
            commentList = savedInstanceState.getParcelableArrayList(COMMENT_LIST);
            nextPage = savedInstanceState.getString(NEXT_PAGE);
        }

        presenter = new CommentsPresenterImpl(this, nextPage, commentList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(COMMENT_LIST, commentList);
        outState.putString(NEXT_PAGE, nextPage);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        unbinder = ButterKnife.bind(this, view);

        commentAdapter = new CommentAdapter(getActivity(), commentList, null);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(commentAdapter);
        alphaAdapter.setDuration(1000);

        rvAllComments.setAdapter(alphaAdapter);
        rvAllComments.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAllComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    presenter.loadComments();
                }
            }
        });

        setUpToolbar(title);

        ViewCompat.setElevation(llProgressContainer, ELEVATION);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (presenter != null) {
            presenter.cancel();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.enter_left);
        }
        else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.exit_left);
        }
    }

    @Override
    public void onCommentsLoadSuccess(ArrayList<Comment> comments, String currentPage) {
        commentAdapter.update(comments);
        nextPage = currentPage;
    }

    @Override
    public void showProgress() {
        llProgressContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        llProgressContainer.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(@StringRes int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void setUpToolbar(String title) {
        final MainActivity mainActivity = (MainActivity) getActivity();

        if (toolbar != null) {
            mainActivity.setSupportActionBar(toolbar);

            toolbar.setTitle(title);

            if (mainActivity.getSupportActionBar() != null) {
                mainActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCommentsHomePressed();
                }
            });
        }
    }
}
