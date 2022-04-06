package tests

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import config.RestAssuredListener
import helpers.PetsUtility.createPet
import helpers.PetsUtility.deletePet
import helpers.PetsUtility.getPetById
import helpers.PetsUtility.updatePet
import helpers.petGenerator
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import models.Category
import models.EmptyPet
import models.ErrorResponse
import models.Pet
import models.Status
import models.Tag
import java.io.File

class TestPets : FeatureSpec({
    extension(RestAssuredListener)

    feature("Pets CRUD") {
        feature("Get pets") {
            val mapper = jacksonObjectMapper()
            val petsJson = File("./src/test/resources/pets.json").readText()
            val existingPets = mapper.readValue<Array<Pet>>(petsJson)

            // get by status
            // sort of parametrized test
            listOf(Status.Available, Status.Pending, Status.Sold).forEach {
                val currentStatus = it
                scenario("Get pets by ${it} status") {
                    val pets = given()
                        .`when`()
                        .get("/pet/findByStatus?status=${it.status}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getObject(".", Array<Pet>::class.java)

                    pets.toList() shouldContainExactlyInAnyOrder existingPets.filter { it.status == currentStatus.status }
                }
            }


            scenario("Get pets by status with empty status parameter") {
                val pets = RestAssured.given()
                    .`when`()
                    .get("/pet/findByStatus")
                    .then()
                    .statusCode(400)
                    .extract()
                    .body()
                    .asString()

                pets.shouldBe("No status provided. Try again?")
            }

            scenario("Get pets by status with non-existing status parameter") {
                val pets = RestAssured.given()
                    .`when`()
                    .get("/pet/findByStatus?status=ordered")
                    .then()
                    .statusCode(400)
                    .extract()
                    .body()
                    .jsonPath()
                    .getObject(".", ErrorResponse::class.java)

                pets.run {
                    pets.code shouldBe 400
                    pets.message shouldBe "Input error: query parameter `status value `ordered` is not in the allowable values `[available, pending, sold]`"
                }
            }

            // just an example of wrong method check
            // we want to ensure endpoint do not accept unexpected methods
            // it's better to check on lower level of tests
            scenario("Check get pets by status endpoint with wrong method") {
                val pets = given()
                    .`when`()
                    .delete("/pet/findByStatus?status=available")
                    .then()
                    .statusCode(405)
                    .extract()
                    .body()
                    .jsonPath()
                    .getObject(".", ErrorResponse::class.java)

                pets.run {
                    pets.code shouldBe 405
                    pets.message shouldBe "HTTP 405 Method Not Allowed"
                }
            }

            // get by id
            scenario("Get pet by id") {
                val pet = given()
                    .`when`()
                    .get("/pet/3")
                    .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .jsonPath()
                    .getObject(".", Pet::class.java)

                pet.run {
                    id shouldBe 3
                    name shouldBe "Cat 3"
                    category shouldBe Category(2, "Cats")
                    photoUrls shouldBe listOf("url1", "url2")
                    tags shouldBe listOf(Tag(1, "tag3"), Tag(2, "tag4"))
                    status shouldBe Status.Pending.status
                }
            }

            scenario("Get non-existing pet by id") {
                given()
                    .`when`()
                    .get("/pet/545")
                    .then()
                    .statusCode(404)
                    .extract()
                    .body()
                    .asString()
                    .shouldBe("Pet not found")
            }

            scenario("Get pet by id with wrong id") {
                val error = given()
                    .`when`()
                    .get("/pet/asdf")
                    .then()
                    .statusCode(400)
                    .extract()
                    .body()
                    .jsonPath()
                    .getObject(".", ErrorResponse::class.java)
                // TODO: Hmm, this response message doesn't look correct to me, create a bug?
                error.run {
                    code shouldBe 400
                    message shouldBe "Input error: couldn't convert `asdf` to type `class java.lang.Long`"
                }
            }
        }

        feature("Create pets") {
            scenario("Create a new pet") {
                val newPet = petGenerator.next()
                val createdPet = createPet(newPet)
                // TODO: Add custom matcher for Pet
                createdPet.run {
                    id shouldBe newPet.id
                    name shouldBe newPet.name
                    category shouldBe newPet.category
                    photoUrls shouldBe newPet.photoUrls
                    tags shouldBe newPet.tags
                    status shouldBe newPet.status
                }

                // ensure pet is really created
                val getPet = getPetById(newPet.id)

                getPet.run {
                    id shouldBe newPet.id
                    name shouldBe newPet.name
                    category shouldBe newPet.category
                    photoUrls shouldBe newPet.photoUrls
                    tags shouldBe newPet.tags
                    status shouldBe newPet.status
                }

                // delete created pet
                deletePet(newPet.id)
            }

            // TODO: It looks like a bug - new pet rewrite existing record
            // Not sure, should it fails, so, leave as is
            scenario("Create a new pet with existing id") {
                val newPet = Pet(
                    id = 11,
                    name = "Good boy",
                    category = Category(1, "Dogs"),
                    photoUrls = listOf("https://some-url-here.com/dog.png"),
                    tags = listOf(Tag(0, "someTag")),
                    status = Status.Available.status
                )

                createPet(newPet).run {
                    id shouldBe newPet.id
                    name shouldBe newPet.name
                    category shouldBe newPet.category
                    photoUrls shouldBe newPet.photoUrls
                    tags shouldBe newPet.tags
                    status shouldBe newPet.status
                }

                val anotherPet = Pet(
                    id = newPet.id,
                    name = "Bad boy",
                    category = Category(2, "Cats"),
                    photoUrls = listOf("https://another-url.com/cat.png"),
                    tags = listOf(Tag(0, "someAnotherTag")),
                    status = Status.Sold.status
                )

                createPet(anotherPet).run {
                    id shouldBe anotherPet.id
                    name shouldBe anotherPet.name
                    category shouldBe anotherPet.category
                    photoUrls shouldBe anotherPet.photoUrls
                    tags shouldBe anotherPet.tags
                    status shouldBe anotherPet.status
                }

                // ensure pet is really created
                getPetById(anotherPet.id).run {
                    id shouldBe anotherPet.id
                    name shouldBe anotherPet.name
                    category shouldBe anotherPet.category
                    photoUrls shouldBe anotherPet.photoUrls
                    tags shouldBe anotherPet.tags
                    status shouldBe anotherPet.status
                }

                // delete created pet
                deletePet(anotherPet.id)
            }

            // Bug - fails with 500 error
            xscenario("Create a new pet with empty id") {
                val emptyPet = EmptyPet(
                    name = "Good boy",
                    category = Category(1, "Dogs"),
                    photoUrls = listOf("https://some-url-here.com/dog.png"),
                    tags = listOf(Tag(0, "someTag")),
                    status = Status.Available.status
                )
                given()
                    .body(emptyPet)
                    .`when`()
                    .post("/pet")
                    .then()
                    .statusCode(405)
            }
        }

        feature("Delete pets") {
            scenario("Delete pet") {
                val newPet = Pet(
                    id = 11,
                    name = "Good boy",
                    category = Category(1, "Dogs"),
                    photoUrls = listOf("https://some-url-here.com/dog.png"),
                    tags = listOf(Tag(0, "someTag")),
                    status = Status.Available.status
                )
                createPet(newPet)

                deletePet(newPet.id)

                // verify pet deleted
                getPetById(newPet.id, statusCode = 404, response = "Pet not found")
            }

            scenario("Delete non-existing pet") {
                // TODO: Discussable, idempotency for DELETE method
                deletePet(5465)
            }

            scenario("Delete pet with wrong id parameter") {
                given()
                    .`when`()
                    .delete("/pet/somestrangeidhere")
                    .then()
                    .statusCode(400)
                    .extract()
                    .body()
                    .jsonPath()
                    .getObject(".", ErrorResponse::class.java)
                    .run {
                        code shouldBe 400
                        message shouldBe "Input error: couldn't convert `somestrangeidhere` to type `class java.lang.Long`"
                    }
            }
        }

        feature("Update pets") {
            scenario("Update existing pet") {
                val newPet = Pet(
                    id = 11,
                    name = "Good boy",
                    category = Category(1, "Dogs"),
                    photoUrls = listOf("https://some-url-here.com/dog.png"),
                    tags = listOf(Tag(0, "someTag")),
                    status = Status.Available.status
                )

                createPet(newPet)

                val changedPet = newPet.copy(name = "Not so good boy", status = Status.Pending.status)

                updatePet(changedPet).run {
                    id shouldBe changedPet.id
                    name shouldBe changedPet.name
                    category shouldBe changedPet.category
                    photoUrls shouldBe changedPet.photoUrls
                    tags shouldBe changedPet.tags
                    status shouldBe changedPet.status
                }

                // verify pet really changed
                getPetById(changedPet.id).run {
                    id shouldBe changedPet.id
                    name shouldBe changedPet.name
                    category shouldBe changedPet.category
                    photoUrls shouldBe changedPet.photoUrls
                    tags shouldBe changedPet.tags
                    status shouldBe changedPet.status
                }

                // cleanup
                deletePet(changedPet.id)
            }

            scenario("Update non-existing pet") {
                val pet = Pet(
                    id = 11,
                    name = "Good boy",
                    category = Category(1, "Dogs"),
                    photoUrls = listOf("https://some-url-here.com/dog.png"),
                    tags = listOf(Tag(0, "someTag")),
                    status = Status.Available.status
                )
                given()
                    .body(pet)
                    .`when`()
                    .put("/pet")
                    .then()
                    .statusCode(404)
                    .extract()
                    .body()
                    .asString()
                    .shouldBe("Pet not found")
            }

            // TODO: Fails, looks like a bug
            xscenario("Update pet with wrong id") {
                val pet = EmptyPet()
                given()
                    .body(pet)
                    .`when`()
                    .put("/pet")
                    .then()
                    .statusCode(400)
            }

            // TODO: Fails, looks like a bug
            xscenario("Update existing pet with error data") {
                val newPet = Pet(
                    id = 11,
                    name = "Good boy",
                    category = Category(1, "Dogs"),
                    photoUrls = listOf("https://some-url-here.com/dog.png"),
                    tags = listOf(Tag(0, "someTag")),
                    status = Status.Available.status
                )

                createPet(newPet)

                val pet = EmptyPet(id = newPet.id)
                given()
                    .body(pet)
                    .`when`()
                    .put("/pet")
                    .then()
                    .statusCode(405)

                deletePet(newPet.id)
            }
        }
    }
})
