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

import sql.SQLType;

import java.util.Arrays;
import java.util.List;

public class SQLForeignField extends SQLField {

    private final String referencesTable;
    private final List<SQLField> references;
    private final ForeignKeyAction[] foreignKeyActions;

    public SQLForeignField(String tableName, String name, SQLType type, boolean nullable, String referencesTable, List<SQLField> references, ForeignKeyAction... foreignKeyActions) {
        super(tableName, name, type, nullable, SQLKeyType.FOREIGN_KEY);
        this.referencesTable = referencesTable;
        this.references = references;
        this.foreignKeyActions = foreignKeyActions;
    }

    public SQLForeignField(String tableName, String name, SQLType type, String referencesTable, List<SQLField> references, ForeignKeyAction... foreignKeyActions) {
        super(tableName, name, type, SQLKeyType.FOREIGN_KEY);
        this.referencesTable = referencesTable;
        this.references = references;
        this.foreignKeyActions = foreignKeyActions;
    }

    public SQLForeignField(String tableName, SQLField field, String referencesTable, List<SQLField> references, ForeignKeyAction... foreignKeyActions) {
        super(tableName, field.getName(), field.getType(), field.isNullable(), SQLKeyType.FOREIGN_KEY);
        this.referencesTable = referencesTable;
        this.references = references;
        this.foreignKeyActions = foreignKeyActions;
    }

    @Override
    public String getKeyStatement() {
        return this.keyType.toString() + "(`" + this.name + "`) references " +
                this.referencesTable + "(`" + String.join(", ", this.references.stream().map(SQLField::getName).toList()) + "`) " +
                String.join(",", Arrays.stream(this.foreignKeyActions).map(ForeignKeyAction::toString).toList());
    }
}
