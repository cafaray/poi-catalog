package com.walkers.poi.resource

import com.walkers.poi.model.POICreate
import com.walkers.poi.model.POIType
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class POIResourceTest {

    @Test
    fun testCreatePOI() {
        val poi = POICreate(
            name = "Test Tower",
            latitude = 48.8584,
            longitude = 2.2945,
            type = POIType.monument
        )

        given()
            .header("X-API-Key", "test-key")
            .contentType(ContentType.JSON)
            .body(poi)
            .`when`().post("/v1/pois")
            .then()
            .statusCode(201)
            .body("success", `is`(true))
            .body("data.name", `is`("Test Tower"))
    }

    @Test
    fun testListPOIs() {
        given()
            .header("X-API-Key", "test-key")
            .`when`().get("/v1/pois")
            .then()
            .statusCode(200)
            .body("success", `is`(true))
    }

    @Test
    fun testUnauthorized() {
        given()
            .`when`().get("/v1/pois")
            .then()
            .statusCode(401)
            .body("success", `is`(false))
            .body("code", `is`("UNAUTHORIZED"))
    }
}
