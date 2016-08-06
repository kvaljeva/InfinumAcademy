package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BaseResponse<T> {

    @SerializedName("data")
    private T data;

    @SerializedName("included")
    private ArrayList<Data<User>> included;

    public BaseResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ArrayList<Data<User>> getIncluded() {
        return included;
    }

    public void setIncluded(ArrayList<Data<User>> included) {
        this.included = included;
    }
}
