package com.github.flombois.integration.tests.endpoints;


import com.github.flombois.integration.resources.databases.Postgres14TestResource;
import com.github.flombois.models.ingredients.Ingredient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.jose4j.json.internal.json_simple.JSONObject;
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
import static org.hamcrest.Matchers.equalTo;
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
        @DisplayName("When collection resource is requested")
        class CollectionResource {

            @ParameterizedTest
            @DisplayName("Then return the results of the first page by default")
            @CsvSource({
                    "0,9dda6d5e-77d8-4a86-8833-86bdd902e281,Diced tomatoes",
                    "1,2ab1433a-1cd7-4be7-ab48-553cfabd7169,Water",
                    "2,b14ee15e-5c6a-4685-a196-1679a745d722,Rice",
                    "3,f0723ed0-3c7b-4f3d-944b-03f23f17bd6d,Onions",
                    "4,904726ba-9753-4d67-8dd5-afaeb8f76f14,Salt",
            })
            void list(int index, UUID id, String name) {
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

            @ParameterizedTest
            @DisplayName("Then return the results of the second page")
            @CsvSource({
                    "0,9160f904-0399-4a83-9b26-e26a82c7e756,Pepper",
                    "1,2ba5af76-f394-4cb8-a830-98ed022941ed,Chicken",
                    "2,bbc0fe87-ed6d-4c4a-856b-bf32809d4d5d,Tomato sauce",
                    "3,c054e6c0-35d4-4de5-95d2-c3259fbb6683,Garlic",
                    "4,609a57c3-f720-4109-b68f-d55b186f6415,Olive oil"
            })
            void listSecondPage(int index, UUID id, String name) {
                List<Ingredient> ingredients = given()
                        .log().all()
                        .when()
                        .get("/ingredients?page=1")
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

            @ParameterizedTest
            @DisplayName("The return only two records if requested")
            @CsvSource({
                    "0,9dda6d5e-77d8-4a86-8833-86bdd902e281,Diced tomatoes",
                    "1,2ab1433a-1cd7-4be7-ab48-553cfabd7169,Water"
            })
            void listTwoRecords(int index, UUID id, String name) {
                List<Ingredient> ingredients = given()
                        .log().all()
                        .when()
                        .get("/ingredients?size=2")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getList("", Ingredient.class);

                assertNotNull(ingredients);
                assertEquals(2, ingredients.size());
                assertEquals(id, ingredients.get(index).getId());
                assertEquals(name, ingredients.get(index).getName());
            }

            @Test
            @DisplayName("Then return an error if the number of requested records is higher than the maximum allowed")
            void limitMaxReturnedRecords() {
                given()
                        .log().all()
                        .when()
                        .get("/ingredients?size=1000")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .body("title", equalTo("Constraint Violation"))
                        .body("violations[0].field", equalTo("list.pageSize"))
                        .body("violations[0].message", equalTo("must be less than or equal to 20"));

            }

            @Test
            @DisplayName("Then return an error if size parameter is incorrect")
            void incorrectSizeParameter() {
                given()
                        .log().all()
                        .when()
                        .get("/ingredients?size=-1")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .body("title", equalTo("Constraint Violation"))
                        .body("violations[0].field", equalTo("list.pageSize"))
                        .body("violations[0].message", equalTo("must be greater than or equal to 1"));
            }

            @Test
            @DisplayName("Then return an error if requested page does not exist")
            void incorrectPageParameter() {
                given()
                        .log().all()
                        .when()
                        .get("/ingredients?page=500")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_NOT_FOUND);
            }

            @ParameterizedTest
            @DisplayName("Then return results sorted by UUID")
            @CsvSource({
                    "0,03594121-d70d-49ed-9e25-bd02d5b238e4,Potatoes",
                    "1,102ed299-d501-4e4a-9ad6-2e760d6c5bc9,Eggplants",
                    "2,111d1601-bc8a-4320-8bc4-d652b4f4079b,Ground cloves",
                    "3,1450fe41-ed37-4384-8481-99208d939dbc,Buttermilk",
                    "4,180c1909-2f78-46f0-b3b1-b56e5b2fd267,Brown sugar",
            })
            void sortedByUUID(int index, UUID id, String name) {
                List<Ingredient> ingredients = given()
                        .log().all()
                        .when()
                        .get("/ingredients?sort=id")
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

            @ParameterizedTest
            @DisplayName("Then return results sorted by name")
            @CsvSource({
                    "0,d7e123b1-f024-4ab9-aeae-fd271982e759,Almonds",
                    "1,696139fa-6b61-4e3f-9d51-31c28fab936c,Baking powder",
                    "2,71001cb4-e94c-4792-b894-f187febfe46a,Baking soda",
                    "3,3742649d-83e8-44a6-ade9-1aaf24f4dfb6,Bananas",
                    "4,1bf2bf66-b260-496a-bba0-5361ad786786,Basil",
            })
            void sortedByName(int index, UUID id, String name) {
                List<Ingredient> ingredients = given()
                        .log().all()
                        .when()
                        .get("/ingredients?sort=name")
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


        @Nested
        @DisplayName("When single resource is requested")
        class SingleResource {

            @ParameterizedTest
            @DisplayName("Then return the resource matching the requested id")
            @CsvSource({
                    "f521bfbb-a346-456d-9f1b-7e7938915a13,Maple syrup",
                    "438be847-396c-41ec-acbe-b04531177a19,Eggs",
                    "9a433e67-ac49-42a1-b3ea-2c629ffd7746,Coconut",
                    "7ac08ce1-caa0-4b7a-bef6-3d403228e291,Vanilla extract",
                    "38268358-388f-4e2e-9c07-a2787ea0781c,Salted butter",
            })
            void get(UUID id, String name) {
                Ingredient ingredient = given()
                        .log().all()
                        .when()
                        .get("/ingredients/" + id)
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getObject("", Ingredient.class);

                assertNotNull(ingredient);
                assertEquals(id, ingredient.getId());
                assertEquals(name, ingredient.getName());
            }

            @Test
            @DisplayName("Then return error if the requested id does not match an existing resource")
            void notFound() {
                given()
                        .log().all()
                        .when()
                        .get("/ingredients/" + UUID.randomUUID())
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_NOT_FOUND);
            }
        }

        @Nested
        @DisplayName("When resource creation is requested")
        class CreateResource {

            @Test
            @DisplayName("Then return the newly created resource")
            void post() {
                JSONObject requestBody = new JSONObject(Map.of("name", "Dark chocolate"));
                Ingredient ingredient = given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .post("/ingredients")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_CREATED)
                        .extract()
                        .body().jsonPath().getObject("", Ingredient.class);

                assertNotNull(ingredient);
                assertEquals("Dark chocolate", ingredient.getName());
            }

            @Test
            @DisplayName("Then return an error if an ingredient with the same name already exists")
            void alreadyExisting() {
                JSONObject requestBody = new JSONObject(Map.of("name", "Maple syrup"));
                given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .post("/ingredients")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_BAD_REQUEST);
            }

        }


        @Nested
        @DisplayName("When resource update is requested")
        class UpdateResource {

            @Test
            @DisplayName("Then return status code 200 OK")
            void put() {
                JSONObject requestBody = new JSONObject(Map.of("name", "Chili sauce"));
                Ingredient ingredient = given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .put("/ingredients/8009bc67-1b04-41cd-9d9e-a566b3a6b48e")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().jsonPath().getObject("", Ingredient.class);

                assertNotNull(ingredient);
                assertEquals("8009bc67-1b04-41cd-9d9e-a566b3a6b48e", ingredient.getId().toString());
                assertEquals("Chili sauce", ingredient.getName());
            }

            @Test
            @DisplayName("Then return an error if an ingredient with the same name already exists")
            void alreadyExisting() {
                JSONObject requestBody = new JSONObject(Map.of("name", "Maple syrup"));
                given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .put("/ingredients/da6ca80f-a11b-4ada-8c42-47a06d406234")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_BAD_REQUEST);
            }

            @Test
            @DisplayName("Then return error if the requested id does not match an existing resource")
            void notFound() {
                JSONObject requestBody = new JSONObject(Map.of("name", "Beer"));
                given()
                        .log().all()
                        .when()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .put("/ingredients/" + UUID.randomUUID())
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
                        .delete("/ingredients/87a962f6-472b-4314-9dc4-79423619efee")
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
                        .delete("/ingredients/" + UUID.randomUUID())
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_NOT_FOUND);
            }

        }
    }
}
