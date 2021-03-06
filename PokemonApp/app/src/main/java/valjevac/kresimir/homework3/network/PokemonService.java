package valjevac.kresimir.homework3.network;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.models.User;

public interface PokemonService {

    @POST("/api/v1/users")
    Call<BaseResponse<Data<User>>> insertUser(
            @Body BaseResponse<Data<User>> request
    );

    @POST("api/v1/users/login")
    Call<BaseResponse<Data<User>>> loginUser(
            @Body BaseResponse<Data<User>> request
    );

    @DELETE("/api/v1/users/logout")
    Call<Void> logoutUser();

    @GET("/api/v1/pokemons")
    Call<BaseResponse<ArrayList<Data<Pokemon>>>> getPokemons();

    @Multipart
    @POST("/api/v1/pokemons")
    Call<BaseResponse<Data<Pokemon>>> insertPokemon(
            @Part(value = "data[attributes][name]", encoding = "text/plain") String name,
            @Part("data[attributes][height]") double height,
            @Part("data[attributes][weight]") double weight,
            @Part("data[attributes][gender_id]") int genderId,
            @Part("data[attributes][is_default]") boolean isDefault,
            @Part(value = "data[attributes][description]", encoding = "text/plain") String description,
            @Part("data[attributes][type_ids][]") int[] category,
            @Part("data[attributes][move_ids][]") int[] moves,
            @Part("data[attributes][image]\"; filename=\"pokemon.jpg") RequestBody image
    );
}
