package sql.table;

import sql.SQLObject;
import sql.field.SQLField;

import java.util.List;

public interface Table extends SQLObject {

    String getName();
    List<SQLField> getFields();

}
