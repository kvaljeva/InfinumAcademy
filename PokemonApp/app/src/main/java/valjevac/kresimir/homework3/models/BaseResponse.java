package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> {

    @SerializedName("data")
    private T data;

    public BaseResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
