package com.walkers.poi.filter

import com.walkers.poi.model.ErrorResponse
import com.walkers.poi.service.AuthService
import jakarta.inject.Inject
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import org.jboss.logging.Logger

@Provider
class ApiKeyFilter : ContainerRequestFilter {

    @Inject
    lateinit var authService: AuthService

    private val log = Logger.getLogger(ApiKeyFilter::class.java)

    override fun filter(requestContext: ContainerRequestContext) {
        val path = requestContext.uriInfo.path
        if (path.startsWith("q/health") || path.startsWith("v1/auth")) return
        
        val authHeader = requestContext.getHeaderString("Authorization")
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header")
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse(error = "Authorization token is required", code = "UNAUTHORIZED"))
                    .build()
            )
            return
        }

        val token = authHeader.substring(7)
        val decodedToken = authService.verifyToken(token)
        
        if (decodedToken == null) {
            log.warn("Invalid Firebase token")
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse(error = "Invalid or expired token", code = "INVALID_TOKEN"))
                    .build()
            )
            return
        }

        requestContext.setProperty("userId", decodedToken.uid)
        requestContext.setProperty("userEmail", decodedToken.email)
        log.info("Authenticated user: ${decodedToken.email}")
    }
}
