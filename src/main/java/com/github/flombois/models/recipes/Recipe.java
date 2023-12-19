package com.github.flombois.models.recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.flombois.models.BaseEntity;
import com.github.flombois.models.tags.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * Represents a recipe with details such as name, description, servings, quantities, steps, and tags.
 */
@Indexed
@Entity(name = "recipes")
public class Recipe extends BaseEntity {

    @NotBlank
    @Length(max = 255)
    @FullTextField(analyzer = "name")
    @KeywordField(name = "name_sort", sortable = Sortable.YES, normalizer = "sort")
    @Column(nullable = false)
    private String name;

    @FullTextField(analyzer = "english")
    private String description;

    @Positive
    @Max(32)
    @Column(nullable = false)
    private int servings;

    @Size(max = 1024)
    @IndexedEmbedded
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quantities",
            joinColumns = @JoinColumn(name = "recipe_id", nullable = false),
            foreignKey = @ForeignKey(name = "FK_RECIPES_ON_INGREDIENT"))
    private List<@NotNull Quantity> quantities;

    @Size(max = 256)
    @IndexedEmbedded
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "steps",
            joinColumns = @JoinColumn(name = "recipe_id", nullable = false),
            foreignKey = @ForeignKey(name = "FK_STEPS_ON_RECIPE"))
    private List<@NotNull Step> steps;

    @Size(max = 256)
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "recipes_tags",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<@NotNull Tag> tags;

    /**
     * Gets the name of the recipe.
     *
     * @return The name of the recipe.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the recipe.
     *
     * @param name The name of the recipe.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the recipe.
     *
     * @return The description of the recipe.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the recipe.
     *
     * @param description The description of the recipe.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the number of servings for the recipe.
     *
     * @return The number of servings for the recipe.
     */
    public int getServings() {
        return servings;
    }

    /**
     * Sets the number of servings for the recipe.
     *
     * @param servings The number of servings for the recipe.
     */
    public void setServings(int servings) {
        this.servings = servings;
    }

    /**
     * Gets the list of quantities for ingredients in the recipe.
     *
     * @return The list of quantities for ingredients in the recipe.
     */
    public List<Quantity> getQuantities() {
        return quantities;
    }

    /**
     * Sets the list of quantities for ingredients in the recipe.
     *
     * @param quantities The list of quantities for ingredients in the recipe.
     */
    public void setQuantities(List<Quantity> quantities) {
        this.quantities = quantities;
    }

    /**
     * Gets the list of steps to prepare the recipe.
     *
     * @return The list of steps to prepare the recipe.
     */
    public List<Step> getSteps() {
        return steps;
    }

    /**
     * Sets the list of steps to prepare the recipe.
     *
     * @param steps The list of steps to prepare the recipe.
     */
    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    /**
     * Gets the list of tags associated with the recipe.
     *
     * @return The list of tags associated with the recipe.
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Sets the list of tags associated with the recipe.
     *
     * @param tags The list of tags associated with the recipe.
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}