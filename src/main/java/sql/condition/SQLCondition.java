package sql.condition;


import sql.SQLObject;

public interface SQLCondition extends SQLObject {

    public static WhereCondition.Builder where() {
        return WhereCondition.builder();
    }

}
