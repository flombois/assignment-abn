package com.github.flombois.models.recipes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

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

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
