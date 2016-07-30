package valjevac.kresimir.homework3.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.fragments.LoginFragment;
import valjevac.kresimir.homework3.fragments.SignupFragment;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        SignupFragment.OnFragmentInteractionListener {

    private final static String LOGIN_FRAGMENT_TAG = "LoginFragmentTag";

    private final static String SIGNUP_FRAGMENT_TAG = "SignUpFragmentTag";

    private final static int ACTIVITY_RESULT = 420;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        String email = SharedPreferencesHelper.getString(SharedPreferencesHelper.EMAIL);
        String authToken = SharedPreferencesHelper.getString(SharedPreferencesHelper.AUTH_TOKEN);

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(authToken)) {
            openHomeActivity();
        }

        if (!isFragmentActive(LOGIN_FRAGMENT_TAG)) {
            loadFragment(LoginFragment.newInstance(), LOGIN_FRAGMENT_TAG);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();

        if (manager.getBackStackEntryCount() == 1) {
            finish();
        }
        else {
            removeFragment(SIGNUP_FRAGMENT_TAG);
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right);
        transaction.replace(R.id.fl_login_container, fragment, tag);

        if (!isFragmentActive(tag)) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    private boolean isFragmentActive(String tag) {
        FragmentManager manager = getSupportFragmentManager();

        return manager.findFragmentByTag(tag) != null;
    }

    private void removeFragment(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment fragment = manager.findFragmentByTag(tag);

        transaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
        transaction.remove(fragment);
        transaction.commit();

        manager.popBackStack();
    }

    @Override
    public void onLoginButtonClick(boolean isSuccess) {

        if (isSuccess) {
            openHomeActivity();
        }
    }

    @Override
    public void onSignupButtonClick() {
        loadFragment(SignupFragment.newInstance(), SIGNUP_FRAGMENT_TAG);
    }

    @Override
    public void onBackButtonPressed() {
        onBackPressed();
    }

    @Override
    public void onRegisterButtonPressed(boolean isSuccess) {

        if (isSuccess) {
            openHomeActivity();
        }
    }

    private void openHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, PokemonListActivity.class);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ACTIVITY_RESULT) {

                finish();
            }
        }
    }
}
