package com.github.flombois.models.recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.flombois.models.BaseEntity;
import com.github.flombois.models.tags.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity(name = "recipes")
public class Recipe extends BaseEntity {

    @NotBlank
    @Length(max = 255)
    @Column(nullable = false)
    private String name;

    private String description;

    @Positive
    @Max(32)
    @Column(nullable = false)
    private int servings;

    @Size(max = 1024)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quantities",
            joinColumns = @JoinColumn(name = "recipe_id", nullable = false),
            foreignKey = @ForeignKey(name = "FK_RECIPES_ON_INGREDIENT"))
    private List<@NotNull Quantity> quantities;

    @Size(max = 256)
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public List<Quantity> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<Quantity> quantities) {
        this.quantities = quantities;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
