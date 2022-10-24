package sql;

public interface SQLQuery<T> extends SQLObject {

    T load();

}
