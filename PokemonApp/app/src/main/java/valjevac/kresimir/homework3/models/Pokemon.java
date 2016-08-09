package valjevac.kresimir.homework3.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

import valjevac.kresimir.homework3.database.AppDatabase;

@Table(database = AppDatabase.class)
public class Pokemon extends BaseModel implements Parcelable, Serializable {

    @PrimaryKey(autoincrement = false)
    @SerializedName("id")
    private int id;

    @Column
    @SerializedName("name")
    private String name;

    @Column
    @SerializedName("description")
    private String description;

    @Column
    @SerializedName("height")
    private float height;

    @Column
    @SerializedName("weight")
    private float weight;

    @SerializedName("types")
    private String types;

    @SerializedName("moves")
    private String moves;

    @Column
    @SerializedName("image-url")
    private String image;

    @Column
    @SerializedName("gender")
    private String gender;

    @SerializedName("voted-on")
    private int vote;

    @SerializedName("gender_id")
    private int genderId;

    public Pokemon() {

    }

    public Pokemon(String name, String description, float height, float weight, String image, int genderId) {

        this.name = name;
        this.description = description;
        this.height = height;
        this.weight = weight;
        this.image = image;
        this.genderId = genderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getGenderId() {
        return genderId;
    }

    public void setGenderId(int gender) {
        this.genderId = gender;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeFloat(this.height);
        dest.writeFloat(this.weight);
        dest.writeString(this.types);
        dest.writeString(this.moves);
        dest.writeString(this.image);
        dest.writeString(this.gender);
        dest.writeInt(this.vote);
        dest.writeInt(this.genderId);
    }

    protected Pokemon(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.height = in.readFloat();
        this.weight = in.readFloat();
        this.types = in.readString();
        this.moves = in.readString();
        this.image = in.readString();
        this.gender = in.readString();
        this.vote = in.readInt();
        this.genderId = in.readInt();
    }

    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel source) {
            return new Pokemon(source);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };
}
