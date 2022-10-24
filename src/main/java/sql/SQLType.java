package sql;

public interface SQLType {

    String getColName();
    boolean isType(Object o);

    SQLType INTEGER = new SQLType() {
        @Override
        public String getColName() {
            return "INTEGER";
        }

        @Override
        public boolean isType(Object o) {
            return o instanceof Integer;
        }
    };

    SQLType BOOLEAN = new SQLType() {
        @Override
        public String getColName() {
            return "BOOLEAN";
        }

        @Override
        public boolean isType(Object o) {
            return o instanceof Boolean;
        }
    };

}
