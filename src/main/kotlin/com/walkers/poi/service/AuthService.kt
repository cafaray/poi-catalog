package com.walkers.poi.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import com.walkers.poi.model.LoginRequest
import com.walkers.poi.model.LoginResponse
import jakarta.enterprise.context.ApplicationScoped
import org.jboss.logging.Logger

@ApplicationScoped
class AuthService {

    private val log = Logger.getLogger(AuthService::class.java)

    fun verifyToken(token: String): FirebaseToken? {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(token)
        } catch (e: Exception) {
            log.error("Token verification failed: ${e.message}")
            null
        }
    }

    fun login(request: LoginRequest): LoginResponse {
        throw UnsupportedOperationException("Login must be performed via Firebase client SDK")
    }
}
