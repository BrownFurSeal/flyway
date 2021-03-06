/**
 * Copyright (C) 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.flyway.core.dbsupport;

import com.googlecode.flyway.core.util.jdbc.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a database table within a schema.
 */
public abstract class Table {
    /**
     * The Jdbc Template for communicating with the DB.
     */
    protected final JdbcTemplate jdbcTemplate;

    /**
     * The database-specific support.
     */
    protected final DbSupport dbSupport;

    /**
     * The schema this table lives in.
     */
    protected final Schema schema;

    /**
     * The name of the table.
     */
    protected final String name;

    /**
     * Creates a new table.
     *
     * @param jdbcTemplate The Jdbc Template for communicating with the DB.
     * @param dbSupport    The database-specific support.
     * @param schema       The schema this table lives in.
     * @param name         The name of the table.
     */
    public Table(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbSupport = dbSupport;
        this.schema = schema;
        this.name = name;
    }

    /**
     * @return The schema this table lives in.
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * @return The name of the table.
     */
    public String getName() {
        return name;
    }

    /**
     * Drops this table from the database.
     *
     * @throws java.sql.SQLException when the drop failed.
     */
    public abstract void drop() throws SQLException;

    /**
     * Checks whether this table exists.
     *
     * @return {@code true} if it does, {@code false} if not.
     * @throws SQLException when the check failed.
     */
    public abstract boolean exists() throws SQLException;

    /**
     * Checks whether this table is already present in the database. WITHOUT quoting either the table or the schema name!
     *
     * @return {@code true} if the table exists, {@code false} if it doesn't.
     * @throws SQLException when there was an error checking whether this table exists in this schema.
     */
    public abstract boolean existsNoQuotes() throws SQLException;

    /**
     * Checks whether the database contains a table matching these criteria.
     *
     * @param catalog    The catalog where the table resides. (optional)
     * @param schema     The schema where the table resides. (optional)
     * @param table      The name of the table. (optional)
     * @param tableTypes The types of table to look for (ex.: TABLE). (optional)
     * @return {@code true} if a matching table has been found, {@code false} if not.
     * @throws SQLException when the check failed.
     */
    protected boolean exists(Schema catalog, Schema schema, String table, String... tableTypes) throws SQLException {
        String[] types = tableTypes;
        if (types.length == 0) {
            types = null;
        }

        ResultSet resultSet = null;
        boolean found;
        try {
            resultSet = jdbcTemplate.getMetaData().getTables(
                    catalog == null ? null : catalog.getName(),
                    schema == null ? null : schema.getName(),
                    table,
                    types);
            found = resultSet.next();
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }

        return found;
    }

    /**
     * Checks whether the table has a primary key.
     *
     * @return {@code true} if a primary key has been found, {@code false} if not.
     * @throws SQLException when the check failed.
     */
    public boolean hasPrimaryKey() throws SQLException {
        ResultSet resultSet = null;
        boolean found;
        try {
            if (dbSupport.catalogIsSchema()) {
                resultSet = jdbcTemplate.getMetaData().getPrimaryKeys(schema.getName(), null, name);
            } else {
                resultSet = jdbcTemplate.getMetaData().getPrimaryKeys(null, schema.getName(), name);
            }
            found = resultSet.next();
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }

        return found;
    }

    /**
     * Checks whether the database contains a column matching these criteria.
     *
     * @param column The column to look for.
     * @return {@code true} if a matching column has been found, {@code false} if not.
     * @throws SQLException when the check failed.
     */
    public boolean hasColumn(String column) throws SQLException {
        ResultSet resultSet = null;
        boolean found;
        try {
            if (dbSupport.catalogIsSchema()) {
                resultSet = jdbcTemplate.getMetaData().getColumns(schema.getName(), null, name, column);
            } else {
                resultSet = jdbcTemplate.getMetaData().getColumns(null, schema.getName(), name, column);
            }
            found = resultSet.next();
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }

        return found;
    }

    /**
     * Locks this table in this schema using a read/write pessimistic lock until the end of the current transaction.
     *
     * @throws SQLException when this table in this schema could not be locked.
     */
    public abstract void lock() throws SQLException;

    @Override
    public String toString() {
        return dbSupport.quote(schema.getName(), name);
    }
}
