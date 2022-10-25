package sql.statement;

import sql.SQLObject;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;

public interface SQLStatement<T> extends SQLObject {

    IDFinder<Integer> INTEGER_ID_FINDER = results -> results.getInt(1);

    void insert(Connection connection) throws SQLException;

    /**
     * Creates the SQL statement.
     *
     * @return the id if auto-generated, null otherwise
     */
    @Nullable
    <ID> ID insert(Connection connection, IDFinder<ID> idFinder) throws SQLException;

    static <T> SQLiteInsertStatement.Builder<T> sqliteInsert(String tableName) {
        return SQLiteInsertStatement.builder(tableName);
    }

}
