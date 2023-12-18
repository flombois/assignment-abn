package com.github.flombois.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.Optional;

public interface NamedEntityRepository<T> extends PanacheRepository<T> {

    default Optional<T> findByName(String name) {
        return find("name", name).firstResultOptional();
    }
}
