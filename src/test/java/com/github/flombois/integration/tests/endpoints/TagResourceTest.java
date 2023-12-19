package com.github.flombois.integration.tests.endpoints;

import com.github.flombois.integration.resources.databases.Postgres14TestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.List;

@QuarkusTest
@QuarkusTestResource(Postgres14TestResource.class)
@DisplayName("Integration tests for the 'Tag' endpoint")
public class TagResourceTest implements ProvisionDatabase {

    final static List<String> SCRIPTS = List.of(
            "ingredients.sql",
            "tags.sql",
            "recipes.sql",
            "quantities.sql",
            "steps.sql",
            "recipes_tags.sql");

    @Inject
    DataSource dataSource;

    @Override
    public DataSource dataSource() {
        return dataSource;
    }

    @Override
    public List<String> provisionScripts() {
        return SCRIPTS;
    }

    @Override
    public List<String> cleanupScripts() {
        return SCRIPTS;
    }

    @Test
    public void test() {

    }

}
