package com.github.flombois.search;


import com.github.flombois.models.ingredients.Ingredient;
import com.github.flombois.models.recipes.Recipe;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This controller is responsible for handling search queries related to ingredients and recipes
 * using Hibernate Search. It exposes endpoints to perform text-based searches on indexed entities.
 * The controller uses a {@link org.hibernate.search.mapper.orm.session.SearchSession SearchSession}
 * for executing search queries.
 */
@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class SearchController {

    SearchSession searchSession;

    /**
     * Initializes the search index on application startup by triggering mass indexing.
     *
     * @param startupEvent The startup event.
     * @throws InterruptedException If the mass indexing process is interrupted.
     */
    void onStart(@Observes StartupEvent startupEvent) throws InterruptedException {
        getSearchSession().massIndexer().startAndWait();
    }

    /**
     * Performs a search on ingredients based on the provided search pattern.
     *
     * @param pattern The search pattern for ingredients.
     * @param size    Optional parameter specifying the maximum number of results.
     * @return A list of ingredients matching the search criteria.
     */
    @GET
    @Path("/ingredients")
    public List<Ingredient> searchIngredients(@RestQuery String pattern, @RestQuery Optional<Integer> size) {
        return getSearchSession().search(Ingredient.class)
                .where(p -> isValidSearchPattern(pattern) ?
                        p.simpleQueryString().field("name").matching(pattern) :
                        p.matchNone())
                .sort(searchSort -> searchSort.field("name_sort"))
                .fetchHits(size.orElse(10));
    }

    /**
     * Performs a search on recipes based on the provided search pattern.
     *
     * @param pattern The search pattern for recipes.
     * @param size    Optional parameter specifying the maximum number of results.
     * @return A list of recipes matching the search criteria.
     */
    @GET
    @Path("/recipes")
    public List<Recipe> searchRecipes(@RestQuery String pattern, @RestQuery Optional<Integer> size) {
        return getSearchSession().search(Recipe.class)
                .where(p -> isValidSearchPattern(pattern) ?
                        p.simpleQueryString().fields("name", "description").matching(pattern) :
                        p.matchNone())
                .sort(searchSort -> searchSort.field("name_sort"))
                .fetchHits(size.orElse(10));
    }

    /**
     * Checks if the provided search pattern is valid.
     *
     * @param pattern The search pattern.
     * @return {@code true} if the pattern is not null or blank, {@code false} otherwise.
     */
    private boolean isValidSearchPattern(String pattern) {
        return !(Objects.isNull(pattern) || pattern.isBlank());
    }

    /**
     * Retrieves the {@link SearchSession} used for executing search queries.
     *
     * @return The search session.
     */
    protected SearchSession getSearchSession() {
        return searchSession;
    }

    /**
     * Sets the {@link SearchSession} for executing search queries.
     *
     * @param searchSession The search session.
     */
    @Inject
    protected void setSearchSession(SearchSession searchSession) {
        this.searchSession = searchSession;
    }
}