package sql;

public interface SQLStatement<T> extends SQLObject {

    void insert(T t);

}
