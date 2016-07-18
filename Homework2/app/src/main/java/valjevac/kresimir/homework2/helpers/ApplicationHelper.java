package valjevac.kresimir.homework2.helpers;

import android.app.Application;
import android.content.Context;

public class ApplicationHelper extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationHelper.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplicationHelper.context;
    }
}
