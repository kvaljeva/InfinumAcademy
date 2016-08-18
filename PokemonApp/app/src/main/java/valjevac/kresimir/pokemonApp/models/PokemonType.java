package valjevac.kresimir.pokemonApp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PokemonType implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public PokemonType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
