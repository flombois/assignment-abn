package com.github.flombois.integration.tests.endpoints;

import com.github.flombois.integration.resources.databases.Postgres14TestResource;
import com.github.flombois.models.recipes.Recipe;
import com.github.flombois.models.tags.Tag;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(Postgres14TestResource.class)
@DisplayName("Integration tests for the 'Tag' endpoint")
public class TagResourceTest implements ProvisionDatabase {

    final static List<String> SCRIPTS = List.of(
            "ingredients.sql",
            "tags.sql",
            "recipes.sql",
            "quantities.sql",
            "steps.sql",
            "recipes_tags.sql");

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
        @DisplayName("When collection resource is requested")
        class CollectionResource {

            @ParameterizedTest
            @DisplayName("Then return the results of the first page by default")
            @CsvSource({
                    "0,9dda6d5e-77d8-4a86-8833-86bdd902e281,Vegan",
                    "1,2ab1433a-1cd7-4be7-ab48-553cfabd7169,Vegetarian",
                    "2,b14ee15e-5c6a-4685-a196-1679a745d722,Asian",
                    "3,f0723ed0-3c7b-4f3d-944b-03f23f17bd6d,Indian",
                    "4,904726ba-9753-4d67-8dd5-afaeb8f76f14,Italian",
            })
            void list(int index, UUID id, String name) {
                List<Tag> tags = given()
                        .log().all()
                        .when()
                        .get("/tags")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getList("", Tag.class);

                assertNotNull(tags);
                assertEquals(5, tags.size());
                assertEquals(id, tags.get(index).getId());
                assertEquals(name, tags.get(index).getName());
            }

        }

        @Nested
        @DisplayName("When single resource is requested")
        class SingleResource {

            @ParameterizedTest
            @DisplayName("Then return the resource matching the requested id")
            @CsvSource({
                    "5c6ef10f-68b2-4385-bfe9-733196636d06,Holiday",
                    "8ab942f7-e2c0-464d-956f-8c9b6324a78c,Low-Carb",
                    "4fe3d6b8-68e5-44a6-9c75-64d591ee1bf9,Seafood",
                    "bbc0fe87-ed6d-4c4a-856b-bf32809d4d5d,Mexican",
                    "c054e6c0-35d4-4de5-95d2-c3259fbb6683,Mediterranean",
            })
            void get(UUID id, String name) {
                Tag tag = given()
                        .log().all()
                        .when()
                        .get("/tags/" + id)
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getObject("", Tag.class);

                assertNotNull(tag);
                assertEquals(id, tag.getId());
                assertEquals(name, tag.getName());
            }

            @Test
            @DisplayName("Then return the tagged recipes")
            void getRecipes() {
                List<Recipe> recipes =  given()
                        .log().all()
                        .when()
                        .get("/tags/8bc2b290-1bf4-49c9-8f5b-25a5eb535d65/recipes")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getList("", Recipe.class);

                assertNotNull(recipes);
                assertEquals(2, recipes.size());
                assertEquals("9dda6d5e-77d8-4a86-8833-86bdd902e281", recipes.get(0).getId().toString());
                assertEquals("bbc0fe87-ed6d-4c4a-856b-bf32809d4d5d", recipes.get(1).getId().toString());

            }
        }

        @Nested
        @DisplayName("When resource creation is requested")
        class CreateResource {

            @Test
            @DisplayName("Then return the newly created resource")
            void post() {
                Tag tag = given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(Map.of("name", "Spicy"))
                        .post("/tags")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_CREATED)
                        .extract()
                        .body().jsonPath().getObject("", Tag.class);

                assertNotNull(tag);
                assertEquals("Spicy", tag.getName());
            }

        }

        @Nested
        @DisplayName("When resource update is requested")
        class UpdateResource {

            @Test
            @DisplayName("Then return status code 200 OK")
            void put() {
                Tag tag = given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(Map.of("name", "Fall"))
                        .put("/tags/8da77c5c-21b4-4d58-9069-926d3ed6ea54")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getObject("", Tag.class);

                assertNotNull(tag);
                assertEquals("8da77c5c-21b4-4d58-9069-926d3ed6ea54", tag.getId().toString());
                assertEquals("Fall", tag.getName());
            }

            @Test
            @DisplayName("Then return error if the requested id does not match an existing resource")
            void notFound() {
                given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(Map.of("name", "Fall"))
                        .put("/tags/" + UUID.randomUUID())
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_NOT_FOUND);
            }

        }

        @Nested
        @DisplayName("When resource deletion is requested")
        class DeleteResource {

            @Test
            @DisplayName("Then return status code 204 NO CONTENT")
            void delete() {
                given()
                        .log().all()
                        .when()
                        .delete("/tags/9dda6d5e-77d8-4a86-8833-86bdd902e281")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_NO_CONTENT);
            }

            @Test
            @DisplayName("Then return error if the requested id does not match an existing resource")
            void notFound() {
                given()
                        .log().all()
                        .when()
                        .delete("/tags/" + UUID.randomUUID())
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_NOT_FOUND);
            }
        }

    }

}
