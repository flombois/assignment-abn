package com.github.flombois.repositories.recipes;

import com.github.flombois.models.recipes.Recipe;
import com.github.flombois.repositories.NamedEntityRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for managing operations related to Recipe entities.
 */
@ApplicationScoped
public class RecipeRepository implements NamedEntityRepository<Recipe> {

}
