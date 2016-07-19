package valjevac.kresimir.homework2.models;

import java.io.Serializable;

public class UrlModel implements Serializable {
    private String pageName;
    private String url;

    public UrlModel(String pageName, String url) {
        this.pageName = pageName;
        this.url = url;
    }

    public String getPageName() {
        return pageName;
    }

    public String getUrl() {
        return url;
    }
}
