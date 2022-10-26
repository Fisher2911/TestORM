/*
 * MIT License
 *
 * Copyright (c) [year] [fullname]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sql.table;


import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import sql.dialect.SQLDialect;
import sql.dialect.SystemDialect;
import sql.field.ForeignKeyAction;
import sql.field.SQLField;
import sql.field.SQLForeignField;
import sql.field.SQLKeyType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SQLTableImpl implements SQLTable {

    private final String name;
    private final List<SQLField> fields;
    private final Multimap<SQLKeyType, SQLField> keys;

    private SQLTableImpl(String name, List<SQLField> fields) {
        this.name = name;
        this.fields = fields;
        this.keys = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        for (SQLField field : fields) {
            this.keys.put(field.getKeyType(), field);
        }
    }

    @Override
    public String createStatement() {
        final StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(this.name).append(" (");
        int index = 0;
        for (var field : this.fields) {
            builder.append(field.createStatement());
            if (index < this.fields.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        for (var key : this.keys.keySet()) {
            if (key == SQLKeyType.NONE) continue;
            if (key == SQLKeyType.PRIMARY_KEY && SystemDialect.getDialect() == SQLDialect.MYSQL) continue;
            builder.append(", ");
            if (key != SQLKeyType.FOREIGN_KEY) builder.append(key.toString()).append("(");
            int keyIndex = 0;
            for (var field : this.keys.get(key)) {
                if (key == SQLKeyType.FOREIGN_KEY) {
                    builder.append(field.getKeyStatement());
                } else {
                    builder.append("`").append(field.getName()).append("`");
                }
                if (keyIndex < this.keys.get(key).size() - 1) {
                    builder.append(", ");
                }
                keyIndex++;
            }
            if (key != SQLKeyType.FOREIGN_KEY) builder.append(")");
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public void create(Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(this.createStatement());
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<SQLField> getFields() {
        return this.fields;
    }

    @Override
    public SQLForeignField createForeignReference(SQLField field, List<SQLField> idFields, ForeignKeyAction... actions) {
        return new SQLForeignField(field.getTableName(), field, this.name, idFields, actions);
    }

    protected static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private final String name;
        private final List<SQLField> fields = new ArrayList<>();

        private Builder(String name) {
            this.name = name;
        }

        public Builder addField(SQLField field) {
            this.fields.add(field);
            return this;
        }

        public Builder addFields(List<SQLField> fields) {
            this.fields.addAll(fields);
            return this;
        }

        public Builder addFields(SQLField... fields) {
            this.fields.addAll(Arrays.asList(fields));
            return this;
        }

        public SQLTableImpl build() {
            return new SQLTableImpl(this.name, this.fields);
        }
    }
}
