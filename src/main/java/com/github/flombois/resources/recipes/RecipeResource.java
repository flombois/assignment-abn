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

@Path("/recipes")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class RecipeResource {

    private RecipeRepository recipeRepository;
    private TagRepository tagRepository;

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

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") UUID id) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent())
            return Response.ok(optionalRecipe).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}/quantities")
    public Response getQuantities(@PathParam("id") UUID id) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent())
            return Response.ok(optionalRecipe.get().getQuantities()).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

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

    @GET
    @Path("/{id}/steps")
    public Response getSteps(@PathParam("id") UUID id) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent())
            return Response.ok(optionalRecipe.get().getSteps()).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

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

    @GET
    @Path("/{id}/tags")
    public Response getTags(@PathParam("id") UUID id) {
        Optional<Recipe> optionalRecipe = getRecipeRepository().findByIdOptional(id);
        if(optionalRecipe.isPresent())
            return Response.ok(optionalRecipe.get().getTags()).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

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



    protected RecipeRepository getRecipeRepository() {
        return recipeRepository;
    }
    @Inject
    protected void setRecipeRepository(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    protected TagRepository getTagRepository() {
        return tagRepository;
    }

    @Inject
    protected void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
}
