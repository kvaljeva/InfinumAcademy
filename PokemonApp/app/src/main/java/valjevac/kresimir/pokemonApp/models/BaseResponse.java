package valjevac.kresimir.pokemonApp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BaseResponse<T> {

    @SerializedName("data")
    private T data;

    @SerializedName("links")
    private Links links;

    @SerializedName("included")
    private ArrayList<BaseData<User>> included;

    @SerializedName("auth_token")
    private String authToken;

    public BaseResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ArrayList<BaseData<User>> getIncluded() {
        return included;
    }

    public void setIncluded(ArrayList<BaseData<User>> included) {
        this.included = included;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
