package com.stormeye.event.service.storage.domain;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;

/**
 * Utility class for testing domain object metadata field names and indexes.
 *
 * @author ian@meywood.com
 */
public class DomainMetadataUtils {

    /**
     * Asserts that a domain object is has  correctly configured table and field names
     *
     * @param dataSource         the JDBC data source
     * @param tableName          the name of the domain objects table
     * @param expectedFieldNames the expected field names for the domain object
     */
    public static void assertFieldNames(final DataSource dataSource,
                                        final String tableName,
                                        final String... expectedFieldNames) {

        final List<String> columnNames = Arrays.asList(expectedFieldNames);
        final List<String> actualColumnNames = new ArrayList<>();

        try {
            final Connection connection = dataSource.getConnection();
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            final ResultSetMetaData metaData = resultSet.getMetaData();

            assertThat(metaData.getColumnCount(), is(columnNames.size()));

            for (int i = 1; i < columnNames.size() + 1; i++) {
                System.out.println(metaData.getColumnName(i));
                actualColumnNames.add(metaData.getColumnName(i));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (String expected : columnNames) {
            assertThat(actualColumnNames, hasItem(expected));
        }
    }

    /**
     * Asserts that a domain object has  correctly configured indexes
     *
     * @param dataSource      the JDBC data source
     * @param tableName       the name of the domain objects table
     * @param expectedIndexes the expected index names for the domain object
     */
    public static void assertIndexes(final DataSource dataSource,
                                     final String tableName,
                                     final String... expectedIndexes) {

        final List<String> expectedNames = Arrays.asList(expectedIndexes);
        final List<String> actualColumnNames = new ArrayList<>();

        try {
            final Connection connection = dataSource.getConnection();
            final ResultSet indexInfo = connection.getMetaData().getIndexInfo(
                    connection.getCatalog(),
                    connection.getSchema(),
                    tableName,
                    false,
                    true
            );

            while (indexInfo.next()) {
                String dbColumnName = indexInfo.getString("COLUMN_NAME");
                actualColumnNames.add(dbColumnName);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertThat(actualColumnNames.size(), is(expectedNames.size()));

        for (String expected : expectedNames) {
            assertThat(actualColumnNames, hasItem(expected));
        }
    }
}
