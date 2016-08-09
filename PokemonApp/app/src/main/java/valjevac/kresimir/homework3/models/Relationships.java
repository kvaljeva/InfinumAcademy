package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Relationships<T> implements Serializable {

    @SerializedName(value = "author", alternate = "type")
    RelationshipType<T> model;

    public RelationshipType<T> getModel() {
        return model;
    }

    public void setModel(RelationshipType<T> model) {
        this.model = model;
    }
}
