package valjevac.kresimir.pokemonApp.network;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;
import valjevac.kresimir.pokemonApp.models.AuthorData;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.Comment;
import valjevac.kresimir.pokemonApp.models.ExtendedData;
import valjevac.kresimir.pokemonApp.models.Move;
import valjevac.kresimir.pokemonApp.models.MoveData;
import valjevac.kresimir.pokemonApp.models.Pokemon;
import valjevac.kresimir.pokemonApp.models.PokemonType;
import valjevac.kresimir.pokemonApp.models.User;

public interface PokemonService {

    @POST("/api/v1/users")
    Call<BaseResponse<BaseData<User>>> insertUser(
            @Body BaseResponse<BaseData<User>> request
    );

    @POST("api/v1/users/login")
    Call<BaseResponse<BaseData<User>>> loginUser(
            @Body BaseResponse<BaseData<User>> request
    );

    @PUT("/api/v1/users/{id}")
    Call<BaseResponse<BaseData<User>>> updateEmail(
            @Path("id") int userId,
            @Body BaseResponse<BaseData<User>> request
    );

    @DELETE("/api/v1/users/logout")
    Call<Void> logoutUser();

    @DELETE("/api/v1/users/{id}")
    Call<Void> deleteUser(@Path("id") int userId);

    @GET("/api/v1/pokemons")
    Call<BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>>> getPokemons();

    @Multipart
    @POST("/api/v1/pokemons")
    Call<BaseResponse<BaseData<Pokemon>>> insertPokemon(
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

    @DELETE("/api/v1/pokemons/{id}")
    Call<Void> deletePokemon(@Path("id") int pokemonId);

    @POST("/api/v1/pokemons/{id}/upvote")
    Call<BaseResponse<BaseData<Pokemon>>> votePokemon(@Path("id") int pokemonId);

    @POST("/api/v1/pokemons/{id}/downvote")
    Call<BaseResponse<BaseData<Pokemon>>> downvotePokemon(@Path("id") int pokemonId);

    @POST("/api/v1/pokemons/{pokemon_id}/comments")
    Call<BaseResponse<BaseData<Comment>>> insertComment(
            @Path("pokemon_id") int pokemonId,
            @Body BaseResponse<BaseData<Comment>> request
    );

    @GET("/api/v1/pokemons/{pokemon_id}/comments")
    Call<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> getComments(@Path("pokemon_id") int pokemonId);

    @GET
    Call<BaseResponse<ArrayList<ExtendedData<Comment, AuthorData>>>> getCommentsPage(@Url String path);

    @DELETE("/api/v1/pokemons/{pokemon_id}/comments/{id}")
    Call<Void> deleteComment(@Path("pokemon_id") int pokemonId, @Path("id") int commentId);

    @GET
    Call<BaseResponse<ArrayList<ExtendedData<Move, MoveData>>>> getMoves(@Url String path);

    @GET("/api/v1/types")
    Call<BaseResponse<ArrayList<BaseData<PokemonType>>>> getTypes();
}
