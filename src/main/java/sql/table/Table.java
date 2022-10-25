package sql.table;

import sql.SQLObject;
import sql.field.ForeignKeyAction;
import sql.field.SQLField;
import sql.field.SQLForeignField;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface Table extends SQLObject {

    String getName();
    List<SQLField> getFields();
    SQLForeignField createForeignReference(SQLField field, List<SQLField> idFields, ForeignKeyAction... actions);
    void create(Connection connection) throws SQLException;

    static SQLiteTable.Builder sqlite(String name) {
        return SQLiteTable.builder(name);
    }

}
