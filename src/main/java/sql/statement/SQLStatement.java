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

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;

public interface SQLStatement<T> extends SQLObject {

    IDFinder<Integer> INTEGER_ID_FINDER = results -> results.getInt(1);

    void insert(Connection connection) throws SQLException;

    /**
     * Creates the SQL statement.
     *
     * @return the id if auto-generated, null otherwise
     */
    @Nullable
    <ID> ID insert(Connection connection, IDFinder<ID> idFinder) throws SQLException;

    static <T> InsertStatementImpl.Builder<T> sqliteInsert(String tableName) {
        return InsertStatementImpl.builder(tableName);
    }

}
