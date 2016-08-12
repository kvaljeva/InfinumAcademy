package valjevac.kresimir.homework3.interfaces;

import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.User;

public interface SignupListener {

    void onSignupSuccess(BaseResponse<BaseData<User>> body);

    void onSignupError(String error);
}
