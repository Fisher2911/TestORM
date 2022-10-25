package sql.condition;


import sql.Pair;
import sql.SQLObject;

import java.util.List;

public interface SQLCondition extends SQLObject {

    static WhereCondition.Builder where() {
        return WhereCondition.builder();
    }

    List<Pair<Integer, SQLObject>> getInsertionColumns();

}
