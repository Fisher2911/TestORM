package sql.statement;

import sql.SQLObject;
import sql.field.SQLField;

public class SQLJoin implements SQLObject {

    private final SQLField field;
    private final SQLField otherField;
    private final SQLJoinType type;

    public SQLJoin(SQLField field, SQLField otherField, SQLJoinType type) {
        this.field = field;
        this.otherField = otherField;
        this.type = type;
    }

    @Override
    public String createStatement() {
        return this.type.toString() + " `" + otherField.getTableName() + "` ON " + this.field.getTableName() + ".`" + this.field.getName() + "`=" + this.otherField.getTableName() + ".`" + this.otherField.getName() + "`";
    }
}
