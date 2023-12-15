package com.github.flombois.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Entity(name = "tags")
public class Tag extends BaseEntity {

    @NotBlank
    @Length(max = 255)
    @Column(nullable = false, unique = true)
    private String name;
}
