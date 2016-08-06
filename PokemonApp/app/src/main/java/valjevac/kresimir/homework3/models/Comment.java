package valjevac.kresimir.homework3.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Comment implements Serializable {

    @SerializedName("content")
    private String content;

    @SerializedName("created-at")
    private String date;

    private String username;

    private int id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Comment(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
