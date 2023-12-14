package com.github.flombois.resources.postgres;

public class Postgres14TestResource extends PostgresTestResource {
    @Override
    public String imageTag() {
        return "14";
    }
}
