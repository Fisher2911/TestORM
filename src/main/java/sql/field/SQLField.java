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

package sql.field;


import sql.SQLObject;
import sql.SQLType;
import sql.dialect.SQLDialect;
import sql.dialect.SystemDialect;

public class SQLField implements SQLObject {

    protected final String tableName;
    protected final String name;
    protected final SQLType type;
    protected final boolean nullable;
    protected final SQLKeyType keyType;

    public SQLField(String tableName, String name, SQLType type, boolean nullable, SQLKeyType keyType) {
        this.tableName = tableName;
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.keyType = keyType;
    }

    public SQLField(String tableName, String name, SQLType type, SQLKeyType keyType) {
        this(tableName, name, type, false, keyType);
    }

    public SQLField(String tableName, String name, SQLType type) {
        this(tableName, name, type, false, SQLKeyType.NONE);
    }

    public String getTableFieldStatement() {
        return this.createStatement();
    }

    public String getKeyStatement() {
        return this.getTableFieldStatement();
    }

    @Override
    public String createStatement() {
        return "`" + this.name + "` " + this.type.getColName() + (this.nullable ? "" : " NOT NULL");
    }

    public String getTableNameStatement() {
        return "`" + this.tableName + "`";
    }

    public String getTableAndName() {
        return this.tableName + ".`" + this.name + "`";
    }

    public String getAliasName() {
        return this.tableName + "_" + this.name;
    }

    public String getTableName() {
        return tableName;
    }

    public String getName() {
        return name;
    }

    public SQLType getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public SQLKeyType getKeyType() {
        return keyType;
    }
}
