package valjevac.kresimir.homework3.models;

import android.net.Uri;
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

    @Column
    @SerializedName("types")
    private String type;

    @Column
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

    public Pokemon(String name, String description, float height, float weight,
                   String type, String moves, String image, int genderId) {

        this.name = name;
        this.description = description;
        this.height = height;
        this.weight = weight;
        this.type = type;
        this.moves = moves;
        this.image = image;
        this.genderId = genderId;
    }

    private Pokemon(Parcel parcel) {
        this.name = parcel.readString();
        this.description = parcel.readString();
        this.height = parcel.readFloat();
        this.weight = parcel.readFloat();
        this.type = parcel.readString();
        this.moves = parcel.readString();
        this.image = parcel.readParcelable(Uri.class.getClassLoader());
        this.gender = parcel.readString();
        this.vote = parcel.readInt();
        this.genderId = parcel.readInt();
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

    public String getType() {
        return type;
    }

    public void setType(String category) {
        this.type = category;
    }

    public String getMoves() {
        return moves;
    }

    public void setMoves(String abilities) {
        this.moves = abilities;
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeDouble(height);
        parcel.writeDouble(weight);
        parcel.writeString(type);
        parcel.writeString(moves);
        parcel.writeString(image);
        parcel.writeString(gender);
        parcel.writeInt(vote);
        parcel.writeInt(genderId);
    }

    public static final Parcelable.Creator<Pokemon> CREATOR = new Parcelable.Creator<Pokemon>() {
        public Pokemon createFromParcel(Parcel parcel) {
            return new Pokemon(parcel);
        }

        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };
}
