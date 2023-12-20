# assignment-abn

This Java application, built with Java 17 and Quarkus, provides a RESTful API for managing favorite recipes.

## Requirements

### 1. Java Version

- The application is developed using Java 17.

### 2. Quarkus Framework

- Quarkus is used as the underlying framework for building the application.

### 3. Functionality

- The application allows users to add, update, remove, and fetch recipes.
- Users can filter recipes based on the following criteria:
    1. Whether the dish is vegetarian.
    2. The number of servings.
    3. Specific ingredients (include or exclude).
    4. Text search within the instructions.

## Project Structure

The project follows a standard Maven project structure. Key files and directories include:

- `src/main`: Contains the main application code.
- `src/test`: Contains unit and integration tests.
- `pom.xml`: Maven Project Object Model file with project configuration.

## Dependencies

The project includes the following dependencies managed by Quarkus:

- Quarkus Container Image Docker
- Quarkus RestEasy Reactive Jackson
- Quarkus Arc
- Quarkus Hibernate ORM Panache
- Quarkus Hibernate Validator
- Quarkus Liquibase
- Quarkus JDBC PostgreSQL
- Quarkus Config YAML
- Quarkus HAL
- Quarkus Hibernate Search ORM Elasticsearch
- Quarkus JUnit 5

Additionally, the following test dependencies are included:

- Testcontainers
- PostgreSQL (Testcontainers)
- Rest Assured

## Build and Run

You can build the project and run the application using Docker:

```bash
sh ./build-demo.sh // Or chmod +x ./build-demo.sh && ./build-demo.sh
```

Use the provided docker-compose file to quickly deploy the application
```bash
docker compose up -d 
```

Make sure to provide Postgresql and elasticsearch passwords in a .env file 

```.env
POSTGRES_USER=recipesdb
POSTGRES_PASSWORD=<postgres_password>
ELASTIC_PASSWORD=<elastic_password>
```

## Design choices

The application includes three independent endpoints:

1. The main 'Recipe' endpoint (/recipes) that manage recipes properties and dependencies
2. A secondary 'Ingredient' endpoint (/ingredients) that manage ingredients so that they can be shared among different recipes
3. A third 'Tag' endpoint (/tags) that allow the creation of recipes' tags making the retrieval of vegetarian recipes easy and allows to extend to any possible tag (for instance 'Asian cuisine', 'Gluten-free' ...)

In addition, comes a search endpoint (/search) to perform full text search based on the recipe and ingredients properties, it uses an Elasticsearch reverse index
to index Recipes and ingredient properties.

