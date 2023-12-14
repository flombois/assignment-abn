package com.github.flombois.integration.databases;

import com.github.flombois.resources.postgres.Postgres14TestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(Postgres14TestResource.class)
public class Postgres14MigrationTest extends PostgresMigrationTest {



}
