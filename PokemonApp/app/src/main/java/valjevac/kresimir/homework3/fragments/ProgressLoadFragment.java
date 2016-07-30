package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.PokemonListActivity;

public class ProgressLoadFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.tv_progress_title)
    TextView tvProgressTitle;

    @BindView(R.id.toolbar_progress_load)
    Toolbar toolbar;

    public ProgressLoadFragment() {

    }

    public static ProgressLoadFragment newInstance(String progressText, String title) {
        ProgressLoadFragment fragment = new ProgressLoadFragment();

        Bundle args = new Bundle();
        args.putString("ProgressText", progressText);
        args.putString("Title", title);

        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_progess_load, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle arguments = getArguments();

        if (toolbar != null) {

            PokemonListActivity activity = (PokemonListActivity) getActivity();

            activity.setSupportActionBar(toolbar);
        }

        if (arguments != null) {
            String title = arguments.getString("Title");
            String progressText = arguments.getString("ProgressText");

            tvProgressTitle.setText(progressText);
            toolbar.setTitle(title);
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
