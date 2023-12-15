package com.github.flombois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

@Entity(name = "quantities")
public class Quantity extends BaseEntity {

    @Positive
    @Max(Integer.MAX_VALUE)
    @Column(nullable = false)
    private int value;

    @Length(max = 255)
    private String symbol;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

}
