package valjevac.kresimir.homework3.helpers;

import android.app.Application;
import android.content.Context;

public class ApplicationHelper extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationHelper.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplicationHelper.context;
    }
}
