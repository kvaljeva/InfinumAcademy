package valjevac.kresimir.homework2.helpers;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class HistoryHelper {
    private final static String FILE_NAME = "web-browser-history.txt";
    static File path, historyFile;
    static Context appContext;

    public static void init(Context context) {
        path = context.getFilesDir();
        historyFile = new File(path, FILE_NAME);
        appContext = context;
    }

    public static void clearHistory() {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(historyFile);

            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
        }
    }

    public static boolean writeToHistory(String url) {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(historyFile, true)));

            writer.println(url);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) writer.close();
        }

        return true;
    }

    public static ArrayList<String> readHistory () {
        if (appContext == null)
            return null;

        ArrayList<String> content = new ArrayList<>();

        try {
            InputStream inputStream = appContext.openFileInput(FILE_NAME);

            if (inputStream != null) {
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(streamReader);

                String url;

                while ((url = bufferedReader.readLine()) != null) {
                    content.add(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.reverse(content);
        return content;
    }
}
