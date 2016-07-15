package valjevac.kresimir.homework3.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PokemonModel implements Parcelable {
    String name;
    String description;
    double height;
    double weight;
    String category;
    String abilities;

    public PokemonModel(String name, String description, double height, double weight,
                        String category, String abilities) {

        this.name = name;
        this.description = description;
        this.height = height;
        this.weight = weight;
        this.category = category;
        this.abilities = abilities;
    }

    private PokemonModel(Parcel parcel) {
        this.name = parcel.readString();
        this.description = parcel.readString();
        this.height = parcel.readDouble();
        this.weight = parcel.readDouble();
        this.category = parcel.readString();
        this.abilities = parcel.readString();
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAbilites() {
        return abilities;
    }

    public void setAbilites(String abilities) {
        this.abilities = abilities;
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
        parcel.writeString(category);
        parcel.writeString(abilities);
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
