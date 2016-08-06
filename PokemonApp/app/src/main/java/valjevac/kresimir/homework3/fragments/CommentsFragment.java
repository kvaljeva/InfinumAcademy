package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.adapters.CommentAdapter;
import valjevac.kresimir.homework3.interfaces.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.Comment;

public class CommentsFragment extends Fragment {
    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    public static String TITLE = "Title";

    public static String COMMENT_LIST = "CommentList";

    public interface OnFragmentInteractionListener {

        void onCommentsHomePressed();
    }

    @BindView(R.id.rv_all_comments)
    RecyclerView rvAllComments;

    @BindView(R.id.toolbar_comments)
    Toolbar toolbar;

    private ArrayList<Comment> commentList;

    CommentAdapter commentAdapter;

    public CommentsFragment() {

    }

    public static CommentsFragment newInstance(String title, ArrayList<Comment> commentList) {
        CommentsFragment fragment = new CommentsFragment();

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putParcelableArrayList(COMMENT_LIST, commentList);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        unbinder = ButterKnife.bind(this, view);

        String title = getActivity().getString(R.string.comments_fragment_default_title);

        Bundle arguments = getArguments();
        if (arguments != null) {

            title = arguments.getString(TITLE) + " " + getActivity().getString(R.string.comments);
            commentList = arguments.getParcelableArrayList(COMMENT_LIST);
        }

        commentAdapter = new CommentAdapter(getActivity(), commentList, new RecyclerViewClickListener<Comment>() {
            @Override
            public void OnClick(Comment object) {

            }
        });

        rvAllComments.setAdapter(commentAdapter);
        rvAllComments.setLayoutManager(new LinearLayoutManager(getActivity()));

        setUpToolbar(title);

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
