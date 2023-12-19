package com.github.flombois.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import java.util.Optional;
import java.util.UUID;

public interface NamedEntityRepository<T> extends PanacheRepositoryBase<T, UUID> {

    default Optional<T> findByName(String name) {
        return find("name", name).firstResultOptional();
    }
}
