package com.github.flombois.models.recipes;

import com.github.flombois.models.BaseEntity;
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

    @NotEmpty
    @Size(max = 1024)
    @ElementCollection
    @CollectionTable(name = "quantities",
            joinColumns = @JoinColumn(name = "recipe_id", nullable = false),
            foreignKey = @ForeignKey(name = "FK_RECIPES_ON_INGREDIENT"))
    private List<@NotNull Quantity> quantities;

    @NotEmpty
    @Size(max = 256)
    @ElementCollection
    @CollectionTable(name = "steps",
            joinColumns = @JoinColumn(name = "recipe_id", nullable = false),
            foreignKey = @ForeignKey(name = "FK_STEPS_ON_RECIPE"))
    @OrderColumn(name = "step_order")
    private List<@NotNull Step> steps;
}
