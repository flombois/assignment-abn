package com.github.flombois.integration.tests.endpoints;

import com.github.flombois.integration.resources.databases.Postgres14TestResource;
import com.github.flombois.models.ingredients.Ingredient;
import com.github.flombois.models.recipes.Quantity;
import com.github.flombois.models.recipes.Recipe;
import com.github.flombois.models.recipes.Step;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(Postgres14TestResource.class)
@DisplayName("Integration tests for the 'Recipe' endpoint")
public class RecipeResourceTest implements ProvisionDatabase {

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
                    "0,9dda6d5e-77d8-4a86-8833-86bdd902e281,Chocolate Chip Cookies,Classic chocolate chip cookies that are soft and chewy.,24",
                    "1,bbc0fe87-ed6d-4c4a-856b-bf32809d4d5d,Tiramisu,Classic Italian dessert with layers of coffee-soaked ladyfingers and mascarpone cream.,8",
                    "2,c054e6c0-35d4-4de5-95d2-c3259fbb6683,Belgian Fries,Classic crispy Belgian-style fries served with your favorite dipping sauce.,4",
                    "3,609a57c3-f720-4109-b68f-d55b186f6415,Dutch Erwtensoep,Hearty split pea soup a traditional Dutch winter dish.,6",
                    "4,180c1909-2f78-46f0-b3b1-b56e5b2fd267,Indian Chicken Tikka Masala,Creamy and flavorful Indian curry with marinated and grilled chicken.,4",

            })
            void list(int index, UUID id, String name, String description, int servings) {
                List<Recipe> recipes = given()
                        .log().all()
                        .when()
                        .get("/recipes")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getList("", Recipe.class);

                assertNotNull(recipes);
                assertEquals(5, recipes.size());
                assertEquals(id, recipes.get(index).getId());
                assertEquals(name, recipes.get(index).getName());
                assertEquals(description, recipes.get(index).getDescription());
                assertEquals(servings, recipes.get(index).getServings());
            }

        }

        @Nested
        @DisplayName("When single resource is requested")
        class SingleResource {

            @ParameterizedTest
            @DisplayName("Then return the resource matching the requested id")
            @CsvSource({
                    "9dda6d5e-77d8-4a86-8833-86bdd902e281,Chocolate Chip Cookies,Classic chocolate chip cookies that are soft and chewy.,24",
                    "bbc0fe87-ed6d-4c4a-856b-bf32809d4d5d,Tiramisu,Classic Italian dessert with layers of coffee-soaked ladyfingers and mascarpone cream.,8",
                    "c054e6c0-35d4-4de5-95d2-c3259fbb6683,Belgian Fries,Classic crispy Belgian-style fries served with your favorite dipping sauce.,4",
                    "609a57c3-f720-4109-b68f-d55b186f6415,Dutch Erwtensoep,Hearty split pea soup a traditional Dutch winter dish.,6",
                    "180c1909-2f78-46f0-b3b1-b56e5b2fd267,Indian Chicken Tikka Masala,Creamy and flavorful Indian curry with marinated and grilled chicken.,4",

            })
            void list(UUID id, String name, String description, int servings) {
                Recipe recipe = given()
                        .log().all()
                        .when()
                        .get("/recipes/" + id)
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getObject("", Recipe.class);

                assertNotNull(recipe);
                assertEquals(id, recipe.getId());
                assertEquals(name, recipe.getName());
                assertEquals(description, recipe.getDescription());
                assertEquals(servings, recipe.getServings());
            }


            @ParameterizedTest
            @DisplayName("Then return the quantities for specified recipe id")
            @CsvSource({
              "0,4,large,03594121-d70d-49ed-9e25-bd02d5b238e4",
              "1,2,cups,9160f904-0399-4a83-9b26-e26a82c7e756",
              "2,0,to taste,904726ba-9753-4d67-8dd5-afaeb8f76f14",
            })
            void getQuantities(int index, int value, String symbol, UUID ingredientId) {
                List<Quantity> quantities = given()
                        .log().all()
                        .when()
                        .get("/recipes/c054e6c0-35d4-4de5-95d2-c3259fbb6683/quantities")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getList("", Quantity.class);

                assertNotNull(quantities);
                assertEquals(3, quantities.size());
                assertEquals(value, quantities.get(index).getValue());
                assertEquals(symbol, quantities.get(index).getSymbol());
                assertEquals(ingredientId, quantities.get(index).getIngredient().getId());
            }

            @ParameterizedTest
            @DisplayName("Then return the steps for specified recipe id")
            @CsvSource({
                    "0,1,Brew a strong cup of coffee and set aside to cool.",
                    "1,2,In a mixing bowl whisk together mascarpone and sugar until smooth.",
                    "2,3,Dip ladyfingers into the cooled coffee and layer them in a serving dish.",
                    "3,4,Spread half of the mascarpone mixture over the ladyfingers.",
                    "4,5,Repeat the layers finishing with a dusting of cocoa powder.",
            })
            void getSteps(int index, int stepOrder, String description) {
                List<Step> steps = given()
                        .log().all()
                        .when()
                        .get("/recipes/bbc0fe87-ed6d-4c4a-856b-bf32809d4d5d/steps")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getList("", Step.class);

                assertNotNull(steps);
                assertEquals(5, steps.size());
                assertEquals(stepOrder, steps.get(index).getStepOrder());
                assertEquals(description, steps.get(index).getDescription());
            }

        }

        @Nested
        @DisplayName("When resource creation is requested")
        class CreateResource {

            @Test
            @DisplayName("Then return the newly created resource")
            @SuppressWarnings("unchecked")
            void post() {
                Recipe expectedRecipe = new Recipe();
                expectedRecipe.setName("Mozzarella and Tomato Skewers");
                expectedRecipe.setDescription("These Mozzarella and Tomato Skewers are a delightful and refreshing appetizer");
                expectedRecipe.setServings(1);
                expectedRecipe.setQuantities(Collections.emptyList());
                expectedRecipe.setSteps(Collections.emptyList());

                Recipe createdRecipe = given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(expectedRecipe)
                        .post("/recipes")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_CREATED)
                        .extract()
                        .body().jsonPath().getObject("", Recipe.class);

                assertNotNull(createdRecipe);
                assertEquals(expectedRecipe.getName(), createdRecipe.getName());
                assertEquals(expectedRecipe.getDescription(), createdRecipe.getDescription());
                assertEquals(expectedRecipe.getServings(), createdRecipe.getServings());
                assertEquals(expectedRecipe.getQuantities(), createdRecipe.getQuantities());
                assertEquals(expectedRecipe.getSteps(), createdRecipe.getSteps());
            }


            @Test
            @DisplayName("Then return the added quantity")
            void addQuantity() {
                Ingredient nuts = new Ingredient();
                nuts.setId(UUID.fromString("d7e123b1-f024-4ab9-aeae-fd271982e759"));
                nuts.setName("Nuts");
                Quantity expectedQuantity = new Quantity();
                expectedQuantity.setIngredient(nuts);
                expectedQuantity.setValue(100);
                expectedQuantity.setSymbol("g");

                Quantity createdQuantity = given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(expectedQuantity)
                        .post("/recipes/9dda6d5e-77d8-4a86-8833-86bdd902e281/quantities")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getObject("", Quantity.class);

                assertNotNull(expectedQuantity);
                assertEquals(expectedQuantity.getValue(), createdQuantity.getValue());
                assertEquals(expectedQuantity.getSymbol(), createdQuantity.getSymbol());
                assertEquals(expectedQuantity.getIngredient().getName(), createdQuantity.getIngredient().getName());
            }

            @Test
            @DisplayName("Then return the added step")
            void addStep() {
                Step expectedStep = new Step();
                expectedStep.setDescription("Dip the cookie into fresh milk for a better experience");
                expectedStep.setStepOrder(8);
                Step createdStep = given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(expectedStep)
                        .post("/recipes/9dda6d5e-77d8-4a86-8833-86bdd902e281/steps")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getObject("", Step.class);

                assertNotNull(expectedStep);
                assertEquals(expectedStep.getStepOrder(), createdStep.getStepOrder());
                assertEquals(expectedStep.getDescription(), createdStep.getDescription());
            }
        }
    }
}
