package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("data")
    private Data data;

    public User(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
