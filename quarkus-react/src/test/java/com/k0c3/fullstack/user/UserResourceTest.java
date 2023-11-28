package com.k0c3.fullstack.user;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class UserResourceTest {
  @Test
  @TestSecurity(user = "admin", roles = "admin")
  void list() {
    given()
        .when()
        .get("/api/v1/users")
        .then()
        .statusCode(200)
        .body(
            "$.size()",
            greaterThanOrEqualTo(1),
            "[0].name",
            is("admin"),
            "[0].password",
            nullValue());
  }

  @Test
  @TestSecurity(user = "admin", roles = "admin")
  void create() {
    given()
        .body("{\"name\":\"test\",\"password\":\"test\",\"roles\":[\"user\"]}")
        .contentType(ContentType.JSON)
        .when()
        .post("/api/v1/users")
        .then()
        .statusCode(201)
        .body(
            "name", is("test"),
            "password", nullValue(),
            "created", not(emptyString()));
  }

  @Test
  @TestSecurity(user = "user", roles = "user")
  void createUnauthorized() {
    given()
        .body("{\"name\":\"test-unauthorized\",\"password\" :\"test\",\"roles\":[\"user\"]}")
        .contentType(ContentType.JSON)
        .when()
        .post("/api/v1/users")
        .then()
        .statusCode(403);
  }

  @Test
  @TestSecurity(user = "admin", roles = "admin")
  void createDuplicate() {
    given()
        .body("{\"name\":\"admin\",\"password\":\"admin\",\"roles\":[\"user\"]}")
        .contentType(ContentType.JSON)
        .when()
        .post("/api/v1/users")
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(user = "admin", roles = "admin")
  void update() {
    var user =
        given()
            .body("{\"name\":\"to-update\",\"password\":\"admin\",\"roles\":[\"user\"]}")
            .contentType(ContentType.JSON)
            .when()
            .post("/api/v1/users")
            .as(User.class);
    user.name = "updated";
    given()
        .body(user)
        .contentType(ContentType.JSON)
        .when()
        .put("/api/v1/users/" + user.id)
        .then()
        .statusCode(200)
        .body(
            "name", is("updated"),
            "version", is(user.version));
  }

  @Test
  @TestSecurity(user = "admin", roles = "admin")
  void updateOptimisticLock() {
    given()
        .body("{\"name\":\"updated\",\"version\":1337}")
        .contentType(ContentType.JSON)
        .when()
        .put("/api/v1/users/0")
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(user = "admin", roles = "admin")
  void delete() {
    var toDelete =
        given()
            .body("{\"name\":\"to-delete\",\"password\":\"admin\"}")
            .contentType(ContentType.JSON)
            .post("/api/v1/users")
            .as(User.class);
    given().when().delete("/api/v1/users/" + toDelete.id).then().statusCode(204);

    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/v1/users/" + toDelete.id)
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(user = "admin", roles = "user")
  void changePassword() {
    given()
        .body("{\"currentPassword\": \"admin\", \"newPassword\": \"changed\"}")
        .contentType(ContentType.JSON)
        .when()
        .put("/api/v1/users/self/password")
        .then()
        .statusCode(200);

  /*  given().when().get("/api/v1/users/0")
            .then()
            .statusCode(200)
            .body(
                    "password", is(BcryptUtil.bcryptHash("changed")));

    assertTrue(
        BcryptUtil.matches("changed", User.<User>findById(0L).await().indefinitely().password));*/
  }
}
