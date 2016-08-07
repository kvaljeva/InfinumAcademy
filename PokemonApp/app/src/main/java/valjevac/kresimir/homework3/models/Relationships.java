package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Relationships<T> implements Serializable {

    @SerializedName("type")
    T type;
}
