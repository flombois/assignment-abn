package com.github.flombois.models.recipes;

import com.github.flombois.models.ingredients.Ingredient;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.validator.constraints.Length;

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

//    @IndexedEmbedded
//    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}
