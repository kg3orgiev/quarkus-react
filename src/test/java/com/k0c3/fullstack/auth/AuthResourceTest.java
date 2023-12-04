package com.k0c3.fullstack.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AuthResourceTest {
  @Test//password is quarkus
  void loginValidCredentials() {
    given()
            .body("{\"name\":\"admin\",\"password\":\"admin\"}")
            .contentType(ContentType.JSON)
            .when().post("/api/v1/auth/login")
            .then()
            .statusCode(200)
            .body(not(emptyString()));
  }
  @Test
  void loginInvalidCredentials() {
    given()
            .body("{\"name\":\"admin\",\"password\":\"not-quarkus\"}")
            .contentType(ContentType.JSON)
            .when().post("/api/v1/auth/login")
            .then()
            .statusCode(401);
  }
}
