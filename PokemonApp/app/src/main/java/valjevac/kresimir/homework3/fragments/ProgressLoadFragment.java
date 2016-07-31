package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import valjevac.kresimir.homework3.R;

public class ProgressLoadFragment extends Fragment {
    private Unbinder unbinder;

    private final static String PROGRESS_TEXT = "ProgressText";

    private final static String TITLE = "Title";

    @BindView(R.id.tv_progress_title)
    TextView tvProgressTitle;

    public ProgressLoadFragment() {

    }

    public static ProgressLoadFragment newInstance(String progressText, String title) {
        ProgressLoadFragment fragment = new ProgressLoadFragment();

        Bundle args = new Bundle();
        args.putString(PROGRESS_TEXT, progressText);
        args.putString(TITLE, title);

        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_progess_load, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle arguments = getArguments();

        if (arguments != null) {
            String progressText = arguments.getString(PROGRESS_TEXT);

            tvProgressTitle.setText(progressText);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
