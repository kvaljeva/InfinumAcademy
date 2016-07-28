package valjevac.kresimir.homework3.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.Pokedex;
import valjevac.kresimir.homework3.models.User;

public interface PokemonService {

    @POST("/api/v1/users")
    Call<BaseResponse> insertUser(
            @Body BaseResponse request
    );

    @POST("api/v1/users/login")
    Call<BaseResponse> loginUser(
            @Body BaseResponse request
    );

    @DELETE("/api/v1/users/logout")
    Call<Void> logoutUser();

    @GET("/api/v1/pokemons")
    Call<BaseResponse> getPokemons();
}
