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
import valjevac.kresimir.homework3.models.PokemonModel;
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
    Call<BaseResponse<ArrayList<Data<PokemonModel>>>> getPokemons();

    @Multipart
    @POST("/api/v1/pokemons")
    Call<BaseResponse<Data<PokemonModel>>> insertPokemon(
            @Part(value = "data[attributes][name]", encoding = "text/plain") String name,
            @Part(value = "data[attributes][height]") double height,
            @Part(value = "data[attributes][weight]") double weight,
            @Part(value = "data[attributes][gender_id]") int genderId,
            @Part(value = "data[attributes][is_default]") boolean isDefault,
            @Part(value = "data[attributes][description]", encoding = "text/plain") String description,
            @Part(value = "data[attributes][type_ids][]") int[] category,
            @Part(value = "data[attributes][move_ids][]") int[] moves,
            @Part(value = "data[attributes][image]\"; filename=\"pokemonImage.jpg") RequestBody image
    );
}
