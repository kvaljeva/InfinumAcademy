package valjevac.kresimir.pokemonApp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Relationships<T> implements Serializable {

    @SerializedName(value = "author", alternate = {"type", "types"})
    RelationshipType<T> model;

    @SerializedName("comments")
    RelationshipType<ArrayList<Comment>> comments;

    @SerializedName("moves")
    RelationshipType<ArrayList<Move>> moves;

    public RelationshipType<T> getModel() {
        return model;
    }

    public void setModel(RelationshipType<T> model) {
        this.model = model;
    }

    public RelationshipType<ArrayList<Comment>> getComments() {
        return comments;
    }

    public void setComments(RelationshipType<ArrayList<Comment>> comments) {
        this.comments = comments;
    }

    public RelationshipType<ArrayList<Move>> getMoves() {
        return moves;
    }

    public void setMoves(RelationshipType<ArrayList<Move>> moves) {
        this.moves = moves;
    }
}
