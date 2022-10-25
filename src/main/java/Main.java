import sql.SQLMapper;
import sql.SQLType;
import sql.condition.SQLCondition;
import sql.dialect.SQLDialect;
import sql.dialect.SystemDialect;
import sql.field.ForeignKeyAction;
import sql.field.SQLField;
import sql.field.SQLForeignField;
import sql.field.SQLIdField;
import sql.field.SQLKeyType;
import sql.statement.DeleteStatement;
import sql.statement.SQLJoinType;
import sql.statement.SQLQuery;
import sql.statement.SQLStatement;
import sql.table.SQLTable;
import sql.test.Member;
import sql.test.TestObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String KINGDOMS_TABLE_NAME = "kingdoms";
    private static final SQLField KINGDOMS_ID_FIELD = new SQLIdField(KINGDOMS_TABLE_NAME, "id", SQLType.INTEGER, SQLKeyType.PRIMARY_KEY, true);
    private static final SQLField KINGDOMS_NAME_FIELD = new SQLField(KINGDOMS_TABLE_NAME, "name", SQLType.varchar(16));
    private static final SQLField KINGDOMS_DESCRIPTION_FIELD = new SQLField(KINGDOMS_TABLE_NAME, "description", SQLType.varchar(240));

    private static final String MEMBERS_TABLE_NAME = "members";
    private static final SQLField MEMBERS_NAME_FIELD = new SQLField(MEMBERS_TABLE_NAME, "name", SQLType.varchar(16));
    private static final SQLField MEMBERS_BALANCE_FIELD = new SQLField(MEMBERS_TABLE_NAME, "balance", SQLType.DOUBLE);
    private static final SQLField MEMBERS_KINGDOM_ID_FIELD = new SQLForeignField(MEMBERS_TABLE_NAME, "kingdom_id", SQLType.INTEGER, KINGDOMS_TABLE_NAME, List.of(KINGDOMS_ID_FIELD), ForeignKeyAction.ON_DELETE_CASCADE);

    public static void main(String[] args) throws SQLException {

        SystemDialect.setDialect(SQLDialect.MYSQL);


        final SQLTable kingdomsTable = SQLTable.
                sqlite(KINGDOMS_TABLE_NAME).
                addFields(KINGDOMS_ID_FIELD, KINGDOMS_NAME_FIELD, KINGDOMS_DESCRIPTION_FIELD).
                build();
        System.out.println(kingdomsTable.createStatement());
        kingdomsTable.create(getConnection());

        final SQLTable membersTable = SQLTable.
                sqlite("members").
                addFields(MEMBERS_NAME_FIELD, MEMBERS_BALANCE_FIELD, MEMBERS_KINGDOM_ID_FIELD).
                build();
        System.out.println(membersTable.createStatement());
        membersTable.create(getConnection());

        final SQLStatement<TestObject> tableInsertStatement = SQLStatement.<TestObject>
                        sqliteInsert(KINGDOMS_TABLE_NAME).
                add(KINGDOMS_NAME_FIELD, "hello").
                add(KINGDOMS_DESCRIPTION_FIELD, "a description of a kingdom").
                build();
        final Integer id = tableInsertStatement.insert(getConnection(), SQLStatement.INTEGER_ID_FINDER);
        System.out.println("Inserted kingdom id: " + id);

        final SQLStatement<TestObject> tableInsertStatement2 = SQLStatement.<TestObject>
                        sqliteInsert(KINGDOMS_TABLE_NAME).
                add(KINGDOMS_NAME_FIELD, "hi").
                add(KINGDOMS_DESCRIPTION_FIELD, "a 2nd description of a kingdom").
                build();
        final Integer id2 = tableInsertStatement2.insert(getConnection(), SQLStatement.INTEGER_ID_FINDER);
        System.out.println("Inserted kingdom id2: " + id2);

        final DeleteStatement statement = DeleteStatement.
                builder(kingdomsTable).
                where(SQLCondition.where().addCondition(KINGDOMS_ID_FIELD, 1).build()).
                where(SQLCondition.where().addCondition(KINGDOMS_NAME_FIELD, "hello").build()).
                build();

        System.out.println(statement.createStatement());
        statement.execute(getConnection());

        //language=SQLite
        String s = "DELETE FROM `kingdoms` WHERE kingdoms.`id` = (1) AND kingdoms.`name` = ('hello')";

        for (int i = 0; i < 10; i++) {
            final SQLStatement<Member> memberSQLStatement = SQLStatement.<Member>
                    sqliteInsert(MEMBERS_TABLE_NAME).
                    add(MEMBERS_NAME_FIELD, "hello" + (i >= 5 ? 1 : i)).
                    add(MEMBERS_BALANCE_FIELD, 100.0).
                    add(MEMBERS_KINGDOM_ID_FIELD, id).
                    build();
            memberSQLStatement.insert(getConnection());
        }

        final SQLQuery<TestObject> query = SQLQuery.<TestObject>
                sqliteSelect(KINGDOMS_TABLE_NAME).
                select(KINGDOMS_ID_FIELD, KINGDOMS_NAME_FIELD, KINGDOMS_DESCRIPTION_FIELD, MEMBERS_NAME_FIELD, MEMBERS_BALANCE_FIELD).
                join(KINGDOMS_ID_FIELD, MEMBERS_KINGDOM_ID_FIELD, SQLJoinType.LEFT_JOIN).
                where(SQLCondition.where().addCondition(KINGDOMS_ID_FIELD, 1).
                        build()).
                build();
        System.out.println(query.createStatement());
        final var all = query.mapTo(getConnection(), TEST_OBJECT_SQL_MAPPER);
        System.out.println(all);
    }

    private static final SQLMapper<TestObject> TEST_OBJECT_SQL_MAPPER = results -> {
        final Map<Integer, TestObject> kingdoms = new HashMap<>();
        while (results.next()) {
            final int kingdomId = results.getInt(KINGDOMS_ID_FIELD.getAliasName());
            final String kingdomName = results.getString(KINGDOMS_NAME_FIELD.getAliasName());
            final String kingdomDescription = results.getString(KINGDOMS_DESCRIPTION_FIELD.getAliasName());
            final String memberName = results.getString(MEMBERS_NAME_FIELD.getAliasName());
            final double memberBalance = results.getInt(MEMBERS_BALANCE_FIELD.getAliasName());
            final TestObject kingdom = kingdoms.computeIfAbsent(kingdomId, k -> new TestObject(kingdomId, kingdomName, kingdomDescription, new ArrayList<>()));
            kingdom.getMembers().add(new Member(memberName, memberBalance));
        }
        return kingdoms.values();
    };


    private static final Path FILE_PATH = Path.of("src", "main", "resources", "database.db");

    private static Connection connection;

    public static Connection getConnection() {
        final File file = FILE_PATH.toFile();
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (connection != null) return connection;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + FILE_PATH);
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
