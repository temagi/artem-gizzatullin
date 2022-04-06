package config

import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder

object RestAssuredListener : BeforeSpecListener {
    override suspend fun beforeSpec(spec: Spec) {
        RestAssured.baseURI = "http://localhost:8080"
        RestAssured.basePath = "/api/v3"
        RestAssured.requestSpecification = RequestSpecBuilder()
            .build()
            .header("Content-Type", "application/json")
            .header("Accept", "*/*")
    }
}
