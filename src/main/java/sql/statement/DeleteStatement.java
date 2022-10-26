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

import sql.SQLObject;
import sql.condition.SQLCondition;
import sql.table.SQLTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteStatement implements SQLObject {

    private final String tableName;
    private final List<SQLCondition> conditions;

    private DeleteStatement(String tableName, List<SQLCondition> conditions) {
        this.tableName = tableName;
        this.conditions = conditions;
    }

    @Override
    public String createStatement() {
        final StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM `").append(this.tableName).append("`");
        if (!this.conditions.isEmpty()) {
            builder.append(" WHERE ");
            int index = 0;
            for (SQLCondition condition : this.conditions) {
                builder.append(condition.createStatement());
                if (index < this.conditions.size() - 1) {
                    builder.append(" AND ");
                }
                index++;
            }
        }
        return builder.toString();
    }

    public void execute(Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(this.createStatement())) {
            int index = 1;
            for (var condition : this.conditions) {
                for (var insertion : condition.getInsertionColumns()) {
                    statement.setObject(index, insertion.second().createStatement());
                    index++;
                }
            }
            statement.execute();
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<SQLCondition> getConditions() {
        return conditions;
    }

    public static Builder builder(String tableName) {
        return new Builder(tableName);
    }

    public static Builder builder(SQLTable table) {
        return builder(table.getName());
    }

    public static class Builder {

        private final String tableName;
        private final List<SQLCondition> conditions;

        private Builder(String tableName) {
            this.tableName = tableName;
            this.conditions = new ArrayList<>();
        }

        public Builder where(SQLCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder where(SQLCondition... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public DeleteStatement build() {
            return new DeleteStatement(this.tableName, this.conditions);
        }

    }
}
