package sql.field;

import sql.SQLType;
import sql.dialect.SQLDialect;
import sql.dialect.SystemDialect;

public class SQLIdField extends SQLField {

    private final boolean autoIncrement;

    public SQLIdField(String tableName, String name, SQLType type, boolean nullable, SQLKeyType keyType, boolean autoIncrement) {
        super(tableName, name, type, nullable, keyType);
        this.autoIncrement = autoIncrement;
    }

    public SQLIdField(String tableName, String name, SQLType type, SQLKeyType keyType, boolean autoIncrement) {
        this(tableName, name, type, false, keyType, autoIncrement);
    }

    @Override
    public String createStatement() {
        final String keyString = this.keyType == SQLKeyType.PRIMARY_KEY && SystemDialect.getDialect() == SQLDialect.MYSQL ? " " + this.keyType : "";
        final String autoIncrementString = this.keyType == SQLKeyType.PRIMARY_KEY && this.autoIncrement && SystemDialect.getDialect() == SQLDialect.MYSQL ? " AUTOINCREMENT" : "";
        return super.createStatement() + keyString + autoIncrementString;
    }
}
