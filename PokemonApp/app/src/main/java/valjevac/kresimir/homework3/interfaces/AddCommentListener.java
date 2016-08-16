package valjevac.kresimir.homework3.interfaces;

import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Comment;

public interface AddCommentListener {

    void onAddCommentSuccess(BaseResponse<BaseData<Comment>> body);

    void onAddCommentFail(String error);
}
