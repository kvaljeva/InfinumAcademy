package valjevac.kresimir.homework3.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.User;

public interface PokemonService {

    @POST("/api/v1/users")
    Call<User> insertUser(
            @Body User user
    );
}
