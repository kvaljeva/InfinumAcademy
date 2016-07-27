package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Data implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("type")
    private String type;

    @SerializedName("attributes")
    private Attributes attributes;

    public Data(int id, String type, Attributes attributes) {
        this.id = id;
        this.type = type;
        this.attributes = attributes;
    }

    public Data(String type, Attributes attributes) {
        this.type = type;
        this.attributes = attributes;
    }

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

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }
}
