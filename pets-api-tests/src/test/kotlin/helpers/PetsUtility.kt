package helpers

import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import models.Pet

object PetsUtility {
    fun deletePet(id: Int) {
        RestAssured.given()
            .`when`()
            .delete("/pet/$id")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString()
            .shouldBe("Pet deleted")
    }

    fun createPet(pet: Pet): Pet {
        return RestAssured.given()
            .body(pet)
            .`when`()
            .post("/pet")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getObject(".", Pet::class.java)
    }

    fun getPetById(id: Int): Pet {
        return RestAssured.given()
            .`when`()
            .get("/pet/$id")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getObject(".", Pet::class.java)
    }

    fun getPetById(id: Int, statusCode: Int, response: String) {
        RestAssured.given()
            .`when`()
            .get("/pet/$id")
            .then()
            .statusCode(statusCode)
            .extract()
            .body()
            .asString()
            .shouldBe(response)
    }

    fun updatePet(pet: Pet): Pet {
        return RestAssured.given()
            .body(pet)
            .`when`()
            .put("/pet")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getObject(".", Pet::class.java)
    }
}
