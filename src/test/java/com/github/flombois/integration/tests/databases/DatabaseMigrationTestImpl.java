package com.github.flombois.integration.tests.databases;

import io.quarkus.liquibase.LiquibaseFactory;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class DatabaseMigrationTestImpl implements DatabaseMigrationTest {

    @Inject
    LiquibaseFactory liquibaseFactory;

    @Override
    public LiquibaseFactory getLiquibaseFactory() {
        assertNotNull(liquibaseFactory);
        return liquibaseFactory;
    }
}
