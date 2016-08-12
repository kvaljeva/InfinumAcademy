package valjevac.kresimir.homework3.mvp.interactors;

import valjevac.kresimir.homework3.interfaces.SignupListener;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.User;

public interface SignupInteractor {

    void register(BaseResponse<BaseData<User>> request, SignupListener listener);

    void cancel();
}
