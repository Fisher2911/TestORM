import sql.SQLType;
import sql.field.SQLField;
import sql.field.SQLKeyType;
import sql.table.SQLiteTable;
import sql.table.Table;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        final Table table = new SQLiteTable(
                "kingdoms",
                List.of(
                        new SQLField("first", SQLType.BOOLEAN, SQLKeyType.UNIQUE),
                        new SQLField("second", SQLType.INTEGER, SQLKeyType.UNIQUE),
                        new SQLField("third", SQLType.BOOLEAN, SQLKeyType.UNIQUE)
                )
        );
        System.out.println(table.getStatement());
    }

}
