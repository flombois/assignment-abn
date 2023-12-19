package com.github.flombois.models.recipes;

import com.github.flombois.models.ingredients.Ingredient;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.validator.constraints.Length;

/**
 * Represents a quantity of an ingredient in a recipe.
 */
@Indexed
@Embeddable
public class Quantity {

    @Positive
    @Max(Integer.MAX_VALUE)
    @Column(nullable = false)
    private int value;

    @NotNull
    @Length(max = 255)
    @Column(nullable = false)
    private String symbol;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    /**
     * Gets the numeric value of the quantity.
     *
     * @return The numeric value of the quantity.
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the numeric value of the quantity.
     *
     * @param value The numeric value of the quantity.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Gets the symbol associated with the quantity.
     *
     * @return The symbol associated with the quantity.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the symbol associated with the quantity.
     *
     * @param symbol The symbol associated with the quantity.
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the ingredient associated with the quantity.
     *
     * @return The ingredient associated with the quantity.
     */
    public Ingredient getIngredient() {
        return ingredient;
    }

    /**
     * Sets the ingredient associated with the quantity.
     *
     * @param ingredient The ingredient associated with the quantity.
     */
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}