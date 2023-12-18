package com.github.flombois.repositories.tags;

import com.github.flombois.models.tags.Tag;
import com.github.flombois.repositories.NamedEntityRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class TagRepository implements NamedEntityRepository<Tag> {

}
