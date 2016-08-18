package valjevac.kresimir.pokemonApp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.activities.MainActivity;
import valjevac.kresimir.pokemonApp.helpers.SharedPreferencesHelper;
import valjevac.kresimir.pokemonApp.mvp.presenters.UserSettingsPresenter;
import valjevac.kresimir.pokemonApp.mvp.presenters.impl.UserSettingsPresenterImpl;
import valjevac.kresimir.pokemonApp.mvp.views.UserSettingsView;

public class UserSettingsFragment extends Fragment implements UserSettingsView {

    private Unbinder unbinder;

    private OnFragmentInteractionListener listener;

    private UserSettingsPresenter presenter;

    private ProgressDialog progressDialog;

    private boolean changesMade;

    private static final String CHANGES_MADE = "ChangesMade";

    @BindView(R.id.tv_account_username)
    TextView tvUsername;

    @BindView(R.id.et_settings_email_address)
    EditText etEmailAddress;

    @BindView(R.id.tb_user_settings)
    Toolbar toolbar;

    @BindView(R.id.btn_save_user_settings)
    Button btnSaveSettings;

    @BindView(R.id.btn_delete_account)
    Button btnDeleteAccount;

    public interface OnFragmentInteractionListener {

        void onSettingsHomePressed();

        void onAccountDeleted();
    }

    public UserSettingsFragment() {
    }

    public static UserSettingsFragment newInstance() {
        return new UserSettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new UserSettingsPresenterImpl(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);
        unbinder = ButterKnife.bind(this, view);

        tvUsername.setText(SharedPreferencesHelper.getString(SharedPreferencesHelper.USER));
        etEmailAddress.setText(SharedPreferencesHelper.getString(SharedPreferencesHelper.EMAIL));

        setUpToolbar();

        if (savedInstanceState != null) {
            changesMade = savedInstanceState.getBoolean(CHANGES_MADE);
        }

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
    public void onDetach() {
        super.onDetach();

        if (listener != null) {
            listener = null;
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
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.enter_right);
        }
        else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.exit_right);
        }
    }

    @Override
    public void onEmailUpdateSuccess(String email) {
        etEmailAddress.setText(email);
    }

    @Override
    public void showProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getActivity().getString(R.string.progress_dialog_hang_on));

        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void showMessage(@StringRes int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_save_user_settings)
    public void onSaveSettingsClick() {
        presenter.updateEmail(etEmailAddress.getText().toString(), changesMade);
    }

    @OnClick(R.id.btn_delete_account)
    public void onDeleteAccountClick() {
        presenter.deleteAccount(SharedPreferencesHelper.getInt(SharedPreferencesHelper.USER_ID));
    }

    @OnCheckedChanged(R.id.cb_delete_account)
    public void onDeleteAccountChecked(boolean isChecked) {
        if (isChecked) {
            showDeleteButton();
        }
        else {
            hideDeleteButton();
        }
    }

    @Override
    public void onAccountDeletedSuccess() {
        listener.onAccountDeleted();
    }

    @OnTextChanged(R.id.et_settings_email_address)
    public void onEmailAddressTextChanged(CharSequence charSequence) {
        changesMade = !TextUtils.isEmpty(charSequence);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(CHANGES_MADE, changesMade);
    }

    private void setUpToolbar() {
        MainActivity mainActivity = (MainActivity) getActivity();

        if (toolbar != null) {
            mainActivity.setSupportActionBar(toolbar);

            toolbar.setTitle(R.string.add_pokemon_toolbar_title);

            if (mainActivity.getSupportActionBar() != null) {
                mainActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSettingsHomePressed();
                }
            });
        }
    }

    private void showDeleteButton() {
        btnDeleteAccount.setVisibility(View.VISIBLE);
        btnSaveSettings.setVisibility(View.GONE);
    }

    private void hideDeleteButton() {
        btnSaveSettings.setVisibility(View.VISIBLE);
        btnDeleteAccount.setVisibility(View.GONE);
    }
}
