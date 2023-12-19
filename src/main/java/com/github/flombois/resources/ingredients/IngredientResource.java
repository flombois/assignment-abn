package com.github.flombois.resources.ingredients;

import com.github.flombois.models.ingredients.Ingredient;
import com.github.flombois.repositories.ingredients.IngredientRepository;
import com.github.flombois.utils.SortQueryBuilder;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Resource class for managing ingredients.
 *
 * @see Ingredient
 * @see IngredientRepository
 *
 */
@Path("/ingredients")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class IngredientResource {

    private IngredientRepository ingredientRepository;

    /**
     * Retrieves a paginated list of ingredients with optional sorting.
     *
     * @param sortQuery List of fields for sorting.
     * @param pageIndex Page index for pagination.
     * @param pageSize  Page size for pagination.
     * @return Response with a paginated list of ingredients.
     */
    @GET
    public Response list(@QueryParam("sort") List<String> sortQuery,
                         @QueryParam("page") @DefaultValue("0") @PositiveOrZero int pageIndex,
                         @QueryParam("size") @DefaultValue("5") @Min(1) @Max(20) int pageSize) {
        Sort sort = SortQueryBuilder.parse(sortQuery);
        List<Ingredient> ingredients = getIngredientRepository().findAll(sort).page(pageIndex, pageSize).list();
        if (ingredients.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(ingredients).build();
    }

    /**
     * Retrieves an ingredient by ID.
     *
     * @param id Ingredient ID.
     * @return Response with the requested ingredient.
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") UUID id) {
         Optional<Ingredient> ingredient =  getIngredientRepository().findByIdOptional(id);
         if(ingredient.isPresent())
             return Response.ok(ingredient.get()).build();
         return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Creates a new ingredient.
     *
     * @param ingredient The ingredient to create.
     * @return Response with the created ingredient.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@Valid Ingredient ingredient) {
        getIngredientRepository().persist(ingredient);
        return Response.status(Response.Status.CREATED).entity(ingredient).build();
    }


    /**
     * Updates an existing ingredient.
     *
     * @param id     Ingredient ID.
     * @param update Updated ingredient data.
     * @return Response with the updated ingredient.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(@PathParam("id") UUID id, @Valid Ingredient update) {
        Optional<Ingredient> optionalIngredient = getIngredientRepository().findByIdOptional(id);
        if(optionalIngredient.isPresent()) {
            Ingredient ingredient = optionalIngredient.get();
            ingredient.setName(update.getName());
            getIngredientRepository().persist(ingredient);
            return Response.ok(ingredient).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Deletes an ingredient by ID.
     *
     * @param id Ingredient ID.
     * @return Response indicating the success of the deletion.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        Optional<Ingredient> optionalIngredient = getIngredientRepository().findByIdOptional(id);
        if(optionalIngredient.isPresent()) {
            Ingredient ingredient = optionalIngredient.get();
            getIngredientRepository().delete(ingredient);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Retrieves the associated {@link IngredientRepository}.
     *
     * @return The ingredient repository.
     */
    protected IngredientRepository getIngredientRepository() {
        return ingredientRepository;
    }

    /**
     * Injects the {@link IngredientRepository}.
     *
     * @param ingredientRepository The ingredient repository to inject.
     */
    @Inject
    protected void setIngredientRepository(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }
}
