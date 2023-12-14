package com.github.flombois.integration.databases;

import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public interface DatabaseMigrationTest {

    LiquibaseFactory getLiquibaseFactory();

    /**
     * Verify that all the changeset migration have been correctly performed
     * @throws LiquibaseException see {@link liquibase.Liquibase#getChangeSetStatuses(Contexts, LabelExpression)}
     */
    @Test
    default void testMigration() throws LiquibaseException {
        LiquibaseFactory liquibaseFactory = getLiquibaseFactory();
        try(var liquibase = liquibaseFactory.createLiquibase()) {
            liquibase.getChangeSetStatuses(liquibaseFactory.createContexts(), liquibaseFactory.createLabels())
                    .forEach(changeSetStatus -> assertTrue(changeSetStatus.getPreviouslyRan()));
        }
    }

}
