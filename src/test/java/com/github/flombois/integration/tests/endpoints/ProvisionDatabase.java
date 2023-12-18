package com.github.flombois.integration.tests.endpoints;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public interface ProvisionDatabase {


    DataSource dataSource();

    List<String> provisionScripts();

    List<String> cleanupScripts();

    default boolean requireProvisioning() {
        return true;
    }

    default boolean requireCleanup() {
        return true;
    }

    /**
     * Locate the scripts according to the specified resource directory and return the resource classpath list
     * @param resourceDirectory Either 'provision' or 'cleanup', any other input results in a returned empty list
     * @return A list of classpath resources or an empty list
     */
    default List<String> scripts(String resourceDirectory) {
        List<String> scripts = Collections.emptyList();
        if("provision".equalsIgnoreCase(resourceDirectory))
            scripts = provisionScripts();
        if("cleanup".equalsIgnoreCase(resourceDirectory))
            scripts = cleanupScripts();
        return scripts.stream().map(script -> String.format("/database/%s/%s", resourceDirectory, script)).toList();
    }

    /**
     * Read the list of resources and return their content as a list
     * @param resourceDirectory Either 'provision' or 'cleanup', any other input results in a returned empty list
     * @return A list of resources content or an empty list
     */
    default List<String> loadSql(String resourceDirectory) {
        return  scripts(resourceDirectory).stream().map(this::readContent).toList();
    }

    /**
     * Read a single resource and return the file content
     * @param resource UTF-8 encoded resource file
     * @return The file content, see {@link IOUtils#resourceToString(String, Charset)}
     */
    default String readContent(String resource) {
        try {
            return IOUtils.resourceToString(resource, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute the resolved scripts
     * @param resourceDirectory Either 'provision' or 'cleanup'
     * @throws SQLException see {@link Statement#executeLargeUpdate(String)}
     */
    default void executeSql(String resourceDirectory) throws SQLException {
        DataSource dataSource = dataSource();
        assertNotNull(dataSource);

        // Try-with resource ensure resource is automatically closed
        try(Connection connection = dataSource.getConnection()) {
            for (String query: loadSql(resourceDirectory)) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeLargeUpdate(query);
                }
            }
        }
    }

    @BeforeEach
    default void provision() throws SQLException {
        if(requireProvisioning())
            executeSql("provision");
    }


    @AfterEach
    default void cleanup() throws SQLException {
        if(requireCleanup())
            executeSql("cleanup");
    }
}
