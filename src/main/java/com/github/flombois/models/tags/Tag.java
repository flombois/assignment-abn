package com.github.flombois.models.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.flombois.models.BaseEntity;
import com.github.flombois.models.recipes.Recipe;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * Represents a tag associated with recipes.
 */
@Entity(name = "tags")
public class Tag extends BaseEntity {

    @NotBlank
    @Length(max = 255)
    @Column(nullable = false, unique = true)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<@NotNull Recipe> recipes;

    /**
     * Gets the name of the tag.
     *
     * @return The name of the tag.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the tag.
     *
     * @param name The name of the tag.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of recipes associated with the tag.
     *
     * @return The list of recipes associated with the tag.
     */
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * Sets the list of recipes associated with the tag.
     *
     * @param recipes The list of recipes associated with the tag.
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}