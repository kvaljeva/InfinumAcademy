package valjevac.kresimir.homework3.interfaces;

public interface DownvoteListener {

    void onDownvoteSuccess();

    void onDownvoteFail(String error);
}
