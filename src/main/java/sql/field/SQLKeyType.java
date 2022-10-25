package sql.field;

public enum SQLKeyType {

    FOREIGN_KEY,
    UNIQUE,
    PRIMARY_KEY,
    NONE;

    public String toString() {
        return super.toString().replace("_", " ");
    }

}
