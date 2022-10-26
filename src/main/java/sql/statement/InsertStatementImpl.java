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

package sql.statement;

import sql.Pair;
import sql.field.SQLField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertStatementImpl<T> implements SQLStatement<T> {

    private final String tableName;
    private final List<Pair<String, Object>> values;

    private InsertStatementImpl(String tableName, List<Pair<String, Object>> values) {
        this.tableName = tableName;
        this.values = values;
    }

    @Override
    public String createStatement() {
        return "INSERT OR REPLACE INTO " +
                this.tableName + " (" +
                this.getFieldsString() + ") VALUES (" +
                this.getValuesString() + ")";
    }

    private String getFieldsString() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (var pair : this.values) {
            builder.append(pair.first());
            if (index < this.values.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        return builder.toString();
    }

    private String getValuesString() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (var pair : this.values) {
            builder.append("?");
            if (index < this.values.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        return builder.toString();
    }

    @Override
    public <ID> ID insert(Connection connection, IDFinder<ID> idFinder) throws SQLException {
        try (var statement = connection.prepareStatement(this.createStatement(), Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            for (var pair : this.values) {
                statement.setObject(index, pair.second());
                index++;
            }
            statement.executeUpdate();
            if (idFinder == null) return null;
            final ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) return idFinder.find(keys);
            return null;
        }
    }

    @Override
    public void insert(Connection connection) throws SQLException {
        this.insert(connection, null);
    }

    protected static <T> Builder<T> builder(String tableName) {
        return new Builder<T>(tableName);
    }

    public static class Builder<T> {

        private final String tableName;
        private final List<Pair<String, Object>> values;

        private Builder(String tableName) {
            this.tableName = tableName;
            this.values = new ArrayList<>();
        }

        public Builder<T> add(SQLField field, Object value) {
            this.values.add(new Pair<>(field.getName(), value));
            return this;
        }

        public Builder<T> addAll(List<Pair<SQLField, Object>> values) {
            for (var pair : values) {
                this.add(pair.first(), pair.second());
            }
            return this;
        }

        @SafeVarargs
        public final Builder<T> addAll(Pair<SQLField, Object>... values) {
            return this.addAll(Arrays.asList(values));
        }

        public InsertStatementImpl<T> build() {
            return new InsertStatementImpl<T>(this.tableName, this.values);
        }
    }
}
