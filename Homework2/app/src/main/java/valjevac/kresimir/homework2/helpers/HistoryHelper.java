package valjevac.kresimir.homework2.helpers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import valjevac.kresimir.homework2.models.UrlModel;

public class HistoryHelper {
    private final static String FILE_NAME = "web-browser-history.json";
    static File path, historyFile;
    static Context appContext;
    static Gson gson;
    static ArrayList<UrlModel> urlsList;

    public static void init() {
        if (appContext == null)
            appContext = ApplicationHelper.getAppContext();

        path = appContext.getFilesDir();
        historyFile = new File(path, FILE_NAME);

        if (gson == null) gson = new Gson();

        urlsList = readHistory(false);

        // In case that there is no current history, initialize the list
        if (urlsList == null) urlsList = new ArrayList<>();
    }

    public static ArrayList<UrlModel> clearHistory() {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(historyFile);
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
        }

        urlsList.clear();

        return urlsList;
    }

    public static boolean writeToHistory(UrlModel url) {
        FileWriter writer = null;

        // In case that we came here from an intent and that no init was called beforehand
        if (urlsList == null) init();

        urlsList.add(url);

        UrlModel[] content = urlsList.toArray(new UrlModel[urlsList.size()]);

        try {
            writer = new FileWriter(historyFile);
            writer.write(gson.toJson(content));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public static ArrayList<UrlModel> readHistory (boolean isViewCall) {
        if (historyFile == null)
            return null;

        UrlModel[] content = null;

        try {
            JsonReader reader = new JsonReader(new FileReader(historyFile));
            content = gson.fromJson(reader, UrlModel[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<UrlModel> urlList = null;

        if (content != null) {
            urlList = new ArrayList<>(Arrays.asList(content));

            if (isViewCall)
                Collections.reverse(urlList);
        }

        return urlList;
    }
}
