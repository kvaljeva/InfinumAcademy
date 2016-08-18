package valjevac.kresimir.pokemonApp.models;

import com.google.gson.annotations.SerializedName;

public class Source {

    @SerializedName("pointer")
    private String pointer;

    public String getPointer() {
        return pointer;
    }

    public void setPointer(String pointer) {
        this.pointer = pointer;
    }
}
