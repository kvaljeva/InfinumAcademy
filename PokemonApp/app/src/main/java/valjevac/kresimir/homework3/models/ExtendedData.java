package valjevac.kresimir.homework3.models;

public class ExtendedData<T, O> extends BaseData<T> {

    Relationships<O> relationships;

    public ExtendedData(int id, String type, T attributes, Relationships<O> relationships) {
        super(id, type, attributes);
        this.relationships = relationships;
    }

    public Relationships<O> getRelationships() {
        return relationships;
    }

    public void setRelationships(Relationships<O> relationships) {
        this.relationships = relationships;
    }
}
