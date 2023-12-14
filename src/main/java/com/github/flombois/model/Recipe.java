package com.github.flombois.model;

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
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<@NotNull Quantity> quantities;

    @NotEmpty
    @Size(max = 256)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipe_id", nullable = false)
    @OrderColumn(name = "step_order")
    private List<@NotNull Step> steps;
}
