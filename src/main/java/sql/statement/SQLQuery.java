package sql.statement;

import sql.SQLMapper;
import sql.SQLObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public interface SQLQuery<T> extends SQLObject {

    Collection<T> mapTo(Connection connection, SQLMapper<T> mapper) throws SQLException;

    static <T> SQLiteSelectStatement.Builder<T> sqliteSelect(String tableName) {
        return SQLiteSelectStatement.builder(tableName);
    }

}
