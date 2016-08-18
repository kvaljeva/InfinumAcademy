package valjevac.kresimir.pokemonApp.interfaces;

import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.Comment;

public interface AddCommentListener {

    void onAddCommentSuccess(BaseResponse<BaseData<Comment>> body);

    void onAddCommentFail(String error);
}
