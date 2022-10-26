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

import sql.SQLMapper;
import sql.condition.SQLCondition;
import sql.field.SQLField;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SelectStatementImpl<T> implements SQLQuery<T> {

    private final String tableName;
    private final List<SQLField> fields;
    private final List<SQLCondition> conditions;
    private final List<SQLJoin> joins;

    private SelectStatementImpl(
            String tableName,
            List<SQLField> fields,
            List<SQLCondition> conditions,
            List<SQLJoin> joins
    ) {
        this.tableName = tableName;
        this.fields = fields;
        this.conditions = conditions;
        this.joins = joins;
    }

    @Override
    public String createStatement() {
        final StringBuilder builder = new StringBuilder("SELECT ").
                append(this.getFieldsString()).append(" FROM ").
                append(this.tableName);
        for (var join : this.joins) {
            builder.append(" ").append(join.createStatement());
        }
        if (!this.conditions.isEmpty()) {
            builder.append(" WHERE ");
            int index = 0;
            for (var condition : this.conditions) {
                builder.append(condition.createStatement());
                if (index < this.conditions.size() - 1) {
                    builder.append(" AND ");
                }
                index++;
            }
        }
        return builder.toString();
    }

    private String getFieldsString() {
        if (fields.isEmpty()) return "*";
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (SQLField field : this.fields) {
            builder.append(field.getTableAndName());
            if (!this.joins.isEmpty()) {
                builder.append(" AS ").append(field.getAliasName());
            }
            if (index < this.fields.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        return builder.toString();
    }

    public Collection<T> mapTo(Connection connection, SQLMapper<T> mapper) throws SQLException {
        try (var statement = connection.prepareStatement(this.createStatement())) {
            int currentIndex = 0;
            for (var condition : this.conditions) {
                for (var insertion : condition.getInsertionColumns()) {
                    currentIndex++;
                    statement.setObject(currentIndex, insertion.second().createStatement());
                }
            }
            return mapper.map(statement.executeQuery());
        }
    }

    protected static <T> Builder<T> builder(String tableName) {
        return new Builder<>(tableName);
    }

    public static class Builder<T> {

        private final String tableName;
        private final List<SQLField> fields;
        private final List<SQLCondition> conditions;
        private final List<SQLJoin> joins;

        private Builder(String tableName) {
            this.tableName = tableName;
            this.fields = new ArrayList<>();
            this.conditions = new ArrayList<>();
            this.joins = new ArrayList<>();
        }

        public Builder<T> select(SQLField... fields) {
            this.fields.addAll(Arrays.asList(fields));
            return this;
        }

        public Builder<T> where(SQLCondition... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public Builder<T> join(SQLJoin... joins) {
            this.joins.addAll(Arrays.asList(joins));
            return this;
        }

        public Builder<T> join(SQLField thisField, SQLField otherField, SQLJoinType type) {
            this.joins.add(new SQLJoin(thisField, otherField, type));
            return this;
        }

        public SelectStatementImpl<T> build() {
            return new SelectStatementImpl<>(this.tableName, this.fields, this.conditions, this.joins);
        }
    }
}
