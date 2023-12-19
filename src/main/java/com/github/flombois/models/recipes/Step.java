package com.github.flombois.models.recipes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

/**
 * Represents a step in the preparation of a recipe with a specific order and description.
 */
@Indexed
@Embeddable
public class Step {

    @Positive
    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @NotBlank
    @FullTextField(analyzer = "english")
    @Column(nullable = false)
    private String description;

    /**
     * Gets the order of the step in the recipe.
     *
     * @return The order of the step.
     */
    public int getStepOrder() {
        return stepOrder;
    }

    /**
     * Sets the order of the step in the recipe.
     *
     * @param stepOrder The order of the step.
     */
    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    /**
     * Gets the description of the step.
     *
     * @return The description of the step.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the step.
     *
     * @param description The description of the step.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}