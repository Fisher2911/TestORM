package sql.condition;


import sql.Pair;

import java.util.List;

public class WhereCondition implements SQLCondition {

    private final List<Pair<String, Object>> conditions;

    public WhereCondition(List<Pair<String, Object>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String getStatement() {
        final StringBuilder builder = new StringBuilder("WHERE ");
        int index = 0;
        for (var pair : this.conditions) {
            builder.append(pair.first()).append(" = ").append(pair.second());
            if (index < this.conditions.size() - 1) {
                builder.append(" AND ");
            }
            index++;
        }
        return builder.toString();
    }
}
