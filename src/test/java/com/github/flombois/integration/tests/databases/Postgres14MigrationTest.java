package com.github.flombois.integration.tests.databases;

import com.github.flombois.integration.resources.databases.Postgres14TestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(Postgres14TestResource.class)
public class Postgres14MigrationTest extends DatabaseMigrationTestImpl {


}
