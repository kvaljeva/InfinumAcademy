package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthorData implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("type")
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
