package valjevac.kresimir.homework3.interfaces;

import java.util.ArrayList;

import valjevac.kresimir.homework3.models.AuthorData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Comment;
import valjevac.kresimir.homework3.models.ExtendedData;

public interface CommentLoadListener {

    void onCommentsLoadSuccess(BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>> body);

    void onCommentsLoadFail(String error);
}
