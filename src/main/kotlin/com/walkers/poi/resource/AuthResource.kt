package com.walkers.poi.resource

import com.walkers.poi.model.ErrorResponse
import com.walkers.poi.model.SuccessResponse
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AuthResource {

    @POST
    @Path("/login")
    fun login(): Response {
        return Response.ok(
            SuccessResponse(
                data = mapOf(
                    "message" to "Use Firebase Authentication SDK to obtain ID token",
                    "instructions" to "Send POST request to Firebase Auth REST API with email/password"
                )
            )
        ).build()
    }

    @GET
    @Path("/info")
    fun info(): Response {
        return Response.ok(
            SuccessResponse(
                data = mapOf(
                    "authMethod" to "Firebase Authentication",
                    "tokenType" to "Bearer",
                    "headerFormat" to "Authorization: Bearer <firebase-id-token>",
                    "documentation" to "https://firebase.google.com/docs/auth/admin/verify-id-tokens"
                )
            )
        ).build()
    }
}
