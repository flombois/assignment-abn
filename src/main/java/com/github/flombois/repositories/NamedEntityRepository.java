package com.github.flombois.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import java.util.Optional;
import java.util.UUID;

/**
 * Generic repository interface for entities with a name property.
 *
 * @param <T> The type of the entity.
 */
public interface NamedEntityRepository<T> extends PanacheRepositoryBase<T, UUID> {

    /**
     * Finds an entity by name and returns an optional result.
     *
     * @param name The name to search for.
     * @return An optional result of the entity with the specified name.
     */
    default Optional<T> findByNameOptional(String name) {
        return find("name", name).firstResultOptional();
    }
}
