package com.github.flombois.repositories.tags;

import com.github.flombois.models.tags.Tag;
import com.github.flombois.repositories.NamedEntityRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for managing operations related to Tag entities.
 */
@ApplicationScoped
public class TagRepository implements NamedEntityRepository<Tag> {

}
