package com.github.flombois.resources.recipes;

import com.github.flombois.models.recipes.Quantity;
import com.github.flombois.models.recipes.Recipe;
import com.github.flombois.models.recipes.Step;
import com.github.flombois.models.tags.Tag;
import com.github.flombois.repositories.recipes.RecipeRepository;
import com.github.flombois.repositories.tags.TagRepository;
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
 * Resource class for managing recipes.
 *
 * @see Recipe
 * @see RecipeRepository
 */
@Path("/recipes")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class RecipeResource {

    private RecipeRepository recipeRepository;
    private TagRepository tagRepository;

    /**
     * Retrieves a paginated list of recipes with optional sorting.
     *
     * @param sortQuery List of fields for sorting.
     * @param pageIndex Page index for pagination.
     * @param pageSize  Page size for pagination.
     * @return Response with a paginated list of recipes.
     */
    @GET
    public Response list(@QueryParam("sort") List<String> sortQuery,
                         @QueryParam("page") @DefaultValue("0") @PositiveOrZero int pageIndex,
                         @QueryParam("size") @DefaultValue("5") @Min(1) @Max(20) int pageSize) {
        Sort sort = SortQueryBuilder.parse(sortQuery);
        List<Recipe> recipes = getRecipeRepository().findAll(sort).page(pageIndex, pageSize).list();
        if(recipes.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(recipes).build();
    }

    /**
     * Retrieves a recipe by ID.
     *
     * @param id Recipe ID.
     * @return Response with the requested recipe.
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") UUID id) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent())
            return Response.ok(optionalRecipe).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Retrieves quantities of a recipe by ID.
     *
     * @param id Recipe ID.
     * @return Response with the quantities of the requested recipe.
     */
    @GET
    @Path("/{id}/quantities")
    public Response getQuantities(@PathParam("id") UUID id) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent())
            return Response.ok(optionalRecipe.get().getQuantities()).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Creates a new recipe.
     *
     * @param recipe The recipe to create.
     * @return Response with the created recipe.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@Valid Recipe recipe) {
        getRecipeRepository().persist(recipe);
        return Response.status(Response.Status.CREATED).entity(recipe).build();
    }

    /**
     * Adds a quantity to a recipe by ID.
     *
     * @param id       Recipe ID.
     * @param quantity The quantity to add.
     * @return Response with the added quantity.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/quantities")
    public Response addQuantity(@PathParam("id") UUID id, @Valid Quantity quantity) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            recipe.getQuantities().add(quantity);
            getRecipeRepository().persist(recipe);
            return Response.ok(quantity).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Updates a quantity of a recipe by ID and index.
     *
     * @param id       Recipe ID.
     * @param index    Index of the quantity to update.
     * @param quantity The updated quantity.
     * @return Response with the updated quantity.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/quantities/{index}")
    public Response updateQuantity(@PathParam("id") UUID id,
                                   @PathParam("index") int index,
                                   @Valid Quantity quantity) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            if(recipe.getQuantities().size() > index) {
                recipe.getQuantities().set(index, quantity);
                getRecipeRepository().persist(recipe);
                return Response.ok(quantity).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Deletes a quantity of a recipe by ID and index.
     *
     * @param id    Recipe ID.
     * @param index Index of the quantity to delete.
     * @return Response indicating the success of the deletion.
     */
    @DELETE
    @Path("/{id}/quantities/{index}")
    public Response deleteQuantity(@PathParam("id") UUID id,
                                   @PathParam("index") @Min(0) int index) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            if(recipe.getQuantities().size() > index) {
                recipe.getQuantities().remove(index);
                getRecipeRepository().persist(recipe);
                return Response.status(Response.Status.NO_CONTENT).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Retrieves steps of a recipe by ID.
     *
     * @param id Recipe ID.
     * @return Response with the steps of the requested recipe.
     */
    @GET
    @Path("/{id}/steps")
    public Response getSteps(@PathParam("id") UUID id) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent())
            return Response.ok(optionalRecipe.get().getSteps()).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Adds a step to a recipe by ID.
     *
     * @param id   Recipe ID.
     * @param step The step to add.
     * @return Response with the added step.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/steps")
    public Response addStep(@PathParam("id") UUID id, @Valid Step step) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            recipe.getSteps().add(step);
            getRecipeRepository().persist(recipe);
            return Response.ok(step).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Updates a step of a recipe by ID and index.
     *
     * @param id    Recipe ID.
     * @param index Index of the step to update.
     * @param step  The updated step.
     * @return Response with the updated step.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/steps/{index}")
    public Response updateStep(@PathParam("id") UUID id,
                               @PathParam("index") int index,
                               @Valid Step step) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            if(recipe.getSteps().size() > index) {
                recipe.getSteps().set(index, step);
                getRecipeRepository().persist(recipe);
                return Response.ok(step).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Deletes a step of a recipe by ID and index.
     *
     * @param id    Recipe ID.
     * @param index Index of the step to delete.
     * @return Response indicating the success of the deletion.
     */
    @DELETE
    @Path("/{id}/steps/{index}")
    public Response deleteStep(@PathParam("id") UUID id,
                               @PathParam("index") int index) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            if(recipe.getSteps().size() > index) {
                recipe.getSteps().remove(index);
                getRecipeRepository().persist(recipe);
                return Response.status(Response.Status.NO_CONTENT).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Retrieves tags associated with a recipe by ID.
     *
     * @param id Recipe ID.
     * @return Response with the tags associated with the requested recipe.
     */
    @GET
    @Path("/{id}/tags")
    public Response getTags(@PathParam("id") UUID id) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent())
            return Response.ok(optionalRecipe.get().getTags()).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Associates a tag with a recipe by ID and tag ID.
     *
     * @param id    Recipe ID.
     * @param tagId Tag ID to associate.
     * @return Response indicating the success of the association.
     */
    @POST
    @Path("/{id}/tags/{tagId}")
    public Response associateTag(@PathParam("id") UUID id,
                                 @PathParam("tagId") UUID tagId) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            Optional<Tag> optionalTag = getTagRepository().findByIdOptional(tagId);
            if(optionalTag.isPresent()) {
                Tag tag = optionalTag.get();
                recipe.getTags().add(tag);
                //tag.getRecipes().add(recipe);
                getRecipeRepository().persist(recipe);
                return Response.status(Response.Status.NO_CONTENT).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Retrieves the RecipeRepository instance.
     *
     * @return The RecipeRepository instance.
     */
    protected RecipeRepository getRecipeRepository() {
        return recipeRepository;
    }

    /**
     * Sets the RecipeRepository instance.
     *
     * @param recipeRepository The RecipeRepository instance to set.
     */
    @Inject
    protected void setRecipeRepository(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Retrieves the TagRepository instance.
     *
     * @return The TagRepository instance.
     */
    protected TagRepository getTagRepository() {
        return tagRepository;
    }

    /**
     * Sets the TagRepository instance.
     *
     * @param tagRepository The TagRepository instance to set.
     */
    @Inject
    protected void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
}
