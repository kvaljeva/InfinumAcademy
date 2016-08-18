package valjevac.kresimir.pokemonApp.interfaces;

import java.util.ArrayList;

import valjevac.kresimir.pokemonApp.models.AuthorData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.Comment;
import valjevac.kresimir.pokemonApp.models.ExtendedData;

public interface CommentLoadListener {

    void onCommentsLoadSuccess(BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>> body);

    void onCommentsLoadFail(String error);
}
