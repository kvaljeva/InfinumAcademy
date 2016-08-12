package valjevac.kresimir.homework3.mvp.views;

import android.support.annotation.StringRes;

public interface BaseView {

    void showProgress();

    void hideProgress();

    void showMessage(@StringRes int message);

    void showMessage(String message);
}
