package valjevac.kresimir.homework3.models;

import java.io.Serializable;

public class PokemonModel implements Serializable {
    String name;
    String description;

    public PokemonModel(String name, String description) {
        this.name = name;
        this.description = description;
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
}
