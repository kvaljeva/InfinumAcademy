package valjevac.kresimir.homework3.interfaces;

import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.User;

public interface LoginListener {

    void onLoginSuccess(BaseResponse<BaseData<User>> body);

    void onLoginError(String error);
}
