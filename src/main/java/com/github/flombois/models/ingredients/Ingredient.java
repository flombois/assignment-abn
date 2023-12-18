package com.github.flombois.models.ingredients;

import com.github.flombois.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Entity(name = "ingredients")
public class Ingredient extends BaseEntity {

    @NotBlank
    @Length(max = 255)
    @Column(nullable = false, unique = true)
    private String name;


    public String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }
}
