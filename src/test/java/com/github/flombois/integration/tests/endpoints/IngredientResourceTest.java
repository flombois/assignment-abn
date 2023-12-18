package com.github.flombois.integration.tests.endpoints;


import com.github.flombois.integration.resources.databases.Postgres14TestResource;
import com.github.flombois.models.ingredients.Ingredient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.reactive.common.util.RestMediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@QuarkusTest
@QuarkusTestResource(Postgres14TestResource.class)
@DisplayName("Integration tests for the 'Ingredient' endpoint")
class IngredientResourceTest implements ProvisionDatabase {

    final static List<String> SCRIPTS = List.of("ingredients.sql");

    @Inject
    DataSource dataSource;


    @Override
    public DataSource dataSource() {
        return dataSource;
    }

    @Override
    public List<String> provisionScripts() {
        return SCRIPTS;
    }

    @Override
    public List<String> cleanupScripts() {
        return SCRIPTS;
    }


    @Nested
    @DisplayName("Given the user is authenticated")
    class Authenticated {


        @Nested
        @DisplayName("When resource collection is requested")
        class Collection {

            @ParameterizedTest
            @DisplayName("Then respond with 200 OK")
            @CsvSource({
                    "0,9dda6d5e-77d8-4a86-8833-86bdd902e281,Diced tomatoes",
                    "1,2ab1433a-1cd7-4be7-ab48-553cfabd7169,Water",
                    "2,b14ee15e-5c6a-4685-a196-1679a745d722,Rice",
                    "3,f0723ed0-3c7b-4f3d-944b-03f23f17bd6d,Onions",
                    "4,904726ba-9753-4d67-8dd5-afaeb8f76f14,Salt",
            })
            void get(int index, UUID id, String name) {
                List<Ingredient> ingredients = given()
                        .log().all()
                        .when()
                        .get("/ingredients")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getList("", Ingredient.class);

                assertNotNull(ingredients);
                assertEquals(5, ingredients.size());
                assertEquals(id, ingredients.get(index).getId());
                assertEquals(name, ingredients.get(index).getName());
            }

        }
    }
}
