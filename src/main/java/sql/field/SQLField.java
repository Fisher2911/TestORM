package sql.field;


import sql.SQLType;

public record SQLField(String name, SQLType type, SQLKeyType keyType) {

}
