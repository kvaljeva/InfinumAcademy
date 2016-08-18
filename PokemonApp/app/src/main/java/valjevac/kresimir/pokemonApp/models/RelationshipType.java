package valjevac.kresimir.pokemonApp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RelationshipType<T> implements Serializable {

    @SerializedName("data")
    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
