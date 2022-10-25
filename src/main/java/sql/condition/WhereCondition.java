package sql.condition;


import sql.Pair;
import sql.SQLObject;
import sql.field.SQLField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            builder.append(pair.first().getTableAndName()).append(" = (").append(pair.second().createStatement()).append(")");
            if (index < this.conditions.size() - 1) {
                builder.append(" AND ");
            }
            index++;
        }
        return builder.toString();
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
