package com.github.flombois.integration.resources.databases;

public class Postgres14TestResource extends PostgresTestResource {
    @Override
    public String imageTag() {
        return "14";
    }
}
