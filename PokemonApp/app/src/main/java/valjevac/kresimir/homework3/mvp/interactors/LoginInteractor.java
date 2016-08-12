package valjevac.kresimir.homework3.mvp.interactors;

import valjevac.kresimir.homework3.interfaces.LoginListener;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.User;

public interface LoginInteractor {

    void login(BaseResponse<BaseData<User>> request, LoginListener listener);

    void cancel();
}
