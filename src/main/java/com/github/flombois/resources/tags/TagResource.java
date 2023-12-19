package com.github.flombois.resources.tags;

import com.github.flombois.models.recipes.Recipe;
import com.github.flombois.models.tags.Tag;
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

@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class TagResource {


    private TagRepository tagRepository;


    @GET
    public Response list(@QueryParam("sort") List<String> sortQuery,
                         @QueryParam("page") @DefaultValue("0") @PositiveOrZero int pageIndex,
                         @QueryParam("size") @DefaultValue("5") @Min(1) @Max(20) int pageSize) {
        Sort sort = SortQueryBuilder.parse(sortQuery);
        List<Tag> tags = getTagRepository().findAll(sort).page(pageIndex, pageSize).list();
        if(tags.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(tags).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") UUID id) {
        Optional<Tag> tagOptional =  getTagRepository().findByIdOptional(id);
        if(tagOptional.isPresent())
            return Response.ok(tagOptional.get()).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}/recipes")
    public Response getRecipesByTagId(@PathParam("id") UUID id) {
        Optional<Tag> tagOptional = getTagRepository().findByIdOptional(id);
        if(tagOptional.isPresent()) {
            List<Recipe> recipes = tagOptional.get().getRecipes();
            return Response.ok(recipes).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{name}/recipes")
    public Response getRecipesByTagName(@PathParam("name") String name) {
        Optional<Tag> tagOptional = getTagRepository().findByNameOptional(name);
        if(tagOptional.isPresent()) {
            List<Recipe> recipes = tagOptional.get().getRecipes();
            return Response.ok(recipes).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@Valid Tag tag) {
        getTagRepository().persist(tag);
        return Response.status(Response.Status.CREATED).entity(tag).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(@PathParam("id") UUID id, @Valid Tag update) {
        Optional<Tag> optionalIngredient = getTagRepository().findByIdOptional(id);
        if(optionalIngredient.isPresent()) {
            Tag tag = optionalIngredient.get();
            tag.setName(update.getName());
            getTagRepository().persist(tag);
            return Response.ok(tag).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        Optional<Tag> optionalTag = getTagRepository().findByIdOptional(id);
        if(optionalTag.isPresent()) {
            Tag tag = optionalTag.get();
            getTagRepository().delete(tag);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    protected TagRepository getTagRepository() {
        return tagRepository;
    }

    @Inject
    protected void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
}
