package sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public interface SQLMapper<T> {

    Collection<T> map(ResultSet resultSet) throws SQLException;

}
