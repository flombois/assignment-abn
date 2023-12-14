package com.github.flombois.resources.postgres;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;
import java.util.Objects;

public abstract class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

    private PostgreSQLContainer<? extends PostgreSQLContainer<?>> container;

    public abstract String imageTag();

    public final String imageName() {
        return "postgres";
    }

    public String image() {
        return imageName().concat(":").concat(imageTag());
    }

    @Override
    public Map<String, String> start() {
        container = new PostgreSQLContainer<>(image());
        container.start();
        return Map.of(
                "quarkus.datasource.username", container.getUsername(),
                "quarkus.datasource.password", container.getPassword(),
                "quarkus.datasource.jdbc.url", container.getJdbcUrl());

    }

    @Override
    public void stop() {
        if(Objects.nonNull(container) && container.isRunning()) {
            container.stop();
        }
    }
}
