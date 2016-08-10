package valjevac.kresimir.homework3.helpers;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;

import valjevac.kresimir.homework3.models.Error;
import valjevac.kresimir.homework3.models.ErrorResponse;

public class ApiErrorHelper {
    private static ErrorResponse errorResponse;

    public static boolean createError(String error) {
        if (TextUtils.isEmpty(error)) {
            return false;
        }

        Gson gson = new Gson();

        try {
            errorResponse = gson.fromJson(error, ErrorResponse.class);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Error> getErrorList() {
        return errorResponse.getErrors();
    }

    public static Error getErrorAt(int index) {
        return errorResponse.getErrors().get(index);
    }

    private static String getErrorSource(int index) {

        String source = errorResponse.getErrors().get(index).getSource().getPointer();

        return source.substring(source.lastIndexOf("/") + 1);
    }

    public static String getFullError(int index) {

        String source = getErrorSource(index);
        String detail = errorResponse.getErrors().get(index).getDetail();

        if (!detail.contains(source)) {

            source = source.substring(0, 1).toUpperCase() + source.substring(1);

            return source + " " + detail;
        }

        detail = detail.substring(0, 1).toUpperCase() + detail.substring(1);

        return detail;
    }
}
