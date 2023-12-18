package com.github.flombois.resources;

import com.github.flombois.models.ingredients.Ingredient;
import com.github.flombois.repositories.ingredients.IngredientRepository;
import com.github.flombois.utils.SortQueryBuilder;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/ingredients")
@Produces(MediaType.APPLICATION_JSON)
public class IngredientResource {

    private IngredientRepository ingredientRepository;

    @GET
    public Response list(@QueryParam("sort") List<String> sortQuery,
                         @QueryParam("page") @DefaultValue("0") @PositiveOrZero int pageIndex,
                         @QueryParam("size") @DefaultValue("5") @Min(1) @Max(20) int pageSize) {
        Sort sort = SortQueryBuilder.parse(sortQuery);
        List<Ingredient> ingredients = getIngredientRepository().findAll(sort).page(pageIndex, pageSize).list();
        return Response.ok(ingredients).build();
    }

    protected IngredientRepository getIngredientRepository() {
        return ingredientRepository;
    }

    @Inject
    protected void setIngredientRepository(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }
}
