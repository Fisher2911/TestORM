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

package sql.condition;


import sql.Pair;
import sql.SQLObject;
import sql.field.SQLField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WhereCondition implements SQLCondition {

    private final List<Pair<SQLField, SQLObject>> conditions;

    public WhereCondition(List<Pair<SQLField, SQLObject>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String createStatement() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (var pair : this.conditions) {
            builder.append(pair.first().getTableAndName()).append(" = (?)");/*.append(pair.second().createStatement()).append(")");*/
            if (index < this.conditions.size() - 1) {
                builder.append(" AND ");
            }
            index++;
        }
        return builder.toString();
    }

    public List<Pair<Integer, SQLObject>> getInsertionColumns() {
        final List<Pair<Integer, SQLObject>> columns = new ArrayList<>();
        int index = 1;
        for (var pair : this.conditions) {
            columns.add(Pair.of(index, pair.second()));
        }
        return columns;
    }

    protected static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Pair<SQLField, SQLObject>> conditions;

        private Builder() {
            this.conditions = new ArrayList<>();
        }

        public Builder addCondition(SQLField field, SQLObject value) {
            this.conditions.add(new Pair<>(field, value));
            return this;
        }

        public Builder addCondition(SQLField field, Object value) {
            this.conditions.add(Pair.of(field, SQLObject.of(value)));
            return this;
        }

        public Builder addConditions(List<Pair<SQLField, SQLObject>> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        @SafeVarargs
        public final Builder addConditions(Pair<SQLField, SQLObject>... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public WhereCondition build() {
            return new WhereCondition(this.conditions);
        }
    }
}
