package valjevac.kresimir.homework2.helpers;

import android.text.TextUtils;
import android.util.Patterns;
import android.webkit.URLUtil;

import java.util.regex.Pattern;

public class UrlHelper {
    private static final String FALLBACK_URL = "http://google.com";

    public static String validateUrl(String url) {
        Pattern URL_PATTERN = Patterns.WEB_URL;

        if (TextUtils.isEmpty(url)) {
            url = FALLBACK_URL;
        }
        else {
            boolean isUrl = URL_PATTERN.matcher(url).matches();

            if (!isUrl) {
                String stringUrl = url + "";

                if (!URLUtil.isNetworkUrl(stringUrl)) {
                    url = FALLBACK_URL + "/search?q=" + stringUrl;
                }
            }

            if (!url.contains("http") && !url.contains("https")) {
                url = "http://" + url;
            }
            else if(url.equals("http://") || url.equals("https://")) {
                url = FALLBACK_URL;
            }
        }

        return url;
    }
}
