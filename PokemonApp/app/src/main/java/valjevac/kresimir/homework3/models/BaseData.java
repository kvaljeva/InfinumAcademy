package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseData<T> implements Serializable {

    @SerializedName("id")
    protected int id;

    @SerializedName("type")
    protected String type;

    @SerializedName("attributes")
    protected T attributes;

    protected BaseData() {
    }

    public BaseData(int id, String type, T attributes) {
        this.id = id;
        this.type = type;
        this.attributes = attributes;
    }

    public BaseData(String type, T attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    public BaseData(T attributes) {
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

    public T getAttributes() {
        return attributes;
    }

    public void setAttributes(T attributes) {
        this.attributes = attributes;
    }
}
