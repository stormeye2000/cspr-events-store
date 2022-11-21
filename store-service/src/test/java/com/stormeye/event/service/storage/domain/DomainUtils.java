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
 * @author ian@meywood.com
 */
public class DomainUtils {

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
