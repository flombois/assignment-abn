package com.github.flombois.search;


import com.github.flombois.models.ingredients.Ingredient;
import com.github.flombois.models.recipes.Recipe;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Path("/search")
@Transactional
public class SearchController {

    SearchSession searchSession;

    void onStart(@Observes StartupEvent startupEvent) throws InterruptedException {
            getSearchSession().massIndexer().startAndWait();
    }

    @GET
    @Path("/ingredients")
    public List<Ingredient> searchIngredients(@RestQuery String pattern, @RestQuery Optional<Integer> size) {
        return getSearchSession().search(Ingredient.class)
                .where(p -> isValidSearchPattern(pattern) ?
                            p.simpleQueryString().field("name").matching(pattern):
                            p.matchNone())
                .sort(searchSort -> searchSort.field("name_sort"))
                .fetchHits(size.orElse(10));
    }

    @GET
    @Path("/recipes")
    public List<Recipe> searchRecipes(@RestQuery String pattern, @RestQuery Optional<Integer> size) {
        return getSearchSession().search(Recipe.class)
                .where(p -> isValidSearchPattern(pattern) ?
                        p.simpleQueryString().fields("name", "description").matching(pattern):
                        p.matchNone())
                .sort(searchSort -> searchSort.field("name_sort"))
                .fetchHits(size.orElse(10));
    }

    private boolean isValidSearchPattern(String pattern) {
        return !(Objects.isNull(pattern) || pattern.isBlank());
    }

    protected SearchSession getSearchSession() {
        return searchSession;
    }

    @Inject
    protected void setSearchSession(SearchSession searchSession) {
        this.searchSession = searchSession;
    }
}
