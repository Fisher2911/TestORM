package sql;

import java.sql.ResultSet;

public interface SQLMapper<T> {

    T map(ResultSet resultSet);

}
