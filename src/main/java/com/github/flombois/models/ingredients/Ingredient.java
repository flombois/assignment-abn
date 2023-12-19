package com.github.flombois.models.ingredients;

import com.github.flombois.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.validator.constraints.Length;

/**
 * Represents an ingredient entity in the Quarkus application.
 */
@Indexed
@Entity(name = "ingredients")
public class Ingredient extends BaseEntity {

    @NotBlank
    @Length(max = 255)
    @FullTextField(analyzer = "name")
    @KeywordField(name = "name_sort", sortable = Sortable.YES, normalizer = "sort")
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Gets the name of the ingredient.
     *
     * @return The name of the ingredient.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the ingredient.
     *
     * @param name The name of the ingredient.
     */
    public void setName(@NotBlank String name) {
        this.name = name;
    }
}