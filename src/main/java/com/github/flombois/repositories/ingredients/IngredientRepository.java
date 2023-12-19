package com.github.flombois.repositories.ingredients;

import com.github.flombois.models.ingredients.Ingredient;
import com.github.flombois.repositories.NamedEntityRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for managing operations related to Ingredient entities.
 */
@ApplicationScoped
public class IngredientRepository implements NamedEntityRepository<Ingredient> {

}
