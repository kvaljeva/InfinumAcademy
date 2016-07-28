package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("data")
    private Data data;

    public BaseResponse(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
