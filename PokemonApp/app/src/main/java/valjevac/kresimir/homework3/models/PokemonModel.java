package valjevac.kresimir.homework3.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PokemonModel implements Parcelable, Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("height")
    private double height;

    @SerializedName("weight")
    private double weight;

    @SerializedName("types")
    private String type;

    @SerializedName("moves")
    private String moves;

    @SerializedName("image-url")
    private String image;

    @SerializedName("gender")
    private String gender;

    public PokemonModel(String name, String description, double height, double weight,
                        String type, String moves, String image, String gender) {

        this.name = name;
        this.description = description;
        this.height = height;
        this.weight = weight;
        this.type = type;
        this.moves = moves;
        this.image = image;
        this.gender = gender;
    }

    private PokemonModel(Parcel parcel) {
        this.name = parcel.readString();
        this.description = parcel.readString();
        this.height = parcel.readDouble();
        this.weight = parcel.readDouble();
        this.type = parcel.readString();
        this.moves = parcel.readString();
        this.image = parcel.readParcelable(Uri.class.getClassLoader());
        this.gender = parcel.readString();
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
    }

    public static final Parcelable.Creator<PokemonModel> CREATOR = new Parcelable.Creator<PokemonModel>() {
        public PokemonModel createFromParcel(Parcel parcel) {
            return new PokemonModel(parcel);
        }

        public PokemonModel[] newArray(int size) {
            return new PokemonModel[size];
        }
    };
}
