package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ErrorResponse {

    @SerializedName("errors")
    ArrayList<Error> errors = new ArrayList<>();

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Error> errors) {
        this.errors = errors;
    }

    public void setError(String details) {
        if (this.errors != null) {
            Error error = new Error();
            error.setDetail(details);

            this.errors.add(error);
        }
    }
}
