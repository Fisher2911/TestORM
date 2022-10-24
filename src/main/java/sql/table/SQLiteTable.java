package sql.table;


import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import sql.field.SQLField;
import sql.field.SQLKeyType;

import javax.security.auth.kerberos.KeyTab;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteTable implements Table {

    private final String name;
    private final List<SQLField> fields;
    private final Multimap<SQLKeyType, SQLField> keys;

    public SQLiteTable(String name, List<SQLField> fields) {
        this.name = name;
        this.fields = fields;
        this.keys = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        for (SQLField field : fields) {
            this.keys.put(field.keyType(), field);
        }
    }

    @Override
    public String getStatement() {
        final StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(this.name).append(" (");
        int index = 0;
        for (var field : this.fields) {
            builder.append("`").append(field.name()).append("` ").append(field.type().getColName());
            if (index < this.fields.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        for (var key : this.keys.keySet()) {
            if (key == SQLKeyType.NONE) continue;
            builder.append(", ").append(key.toString()).append("(");
            int keyIndex = 0;
            for (var field : this.keys.get(key)) {
                builder.append("`").append(field.name()).append("`");
                if (keyIndex < this.keys.get(key).size() - 1) {
                    builder.append(", ");
                }
                keyIndex++;
            }
            builder.append(")");
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<SQLField> getFields() {
        return null;
    }
}
