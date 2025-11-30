package com.walkers.poi.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.cloud.firestore.Firestore
import io.quarkus.runtime.Startup
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

@ApplicationScoped
class FirebaseConfig {

    private val log = Logger.getLogger(FirebaseConfig::class.java)

    @ConfigProperty(name = "firebase.credentials.path")
    lateinit var credentialsPath: String

    @Produces
    @Startup
    fun firestore(): Firestore {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                log.info("Initializing Firebase connection...")
                
                val credFile = File(credentialsPath)
                if (!credFile.exists()) {
                    throw FileNotFoundException("Firebase credentials file not found at: $credentialsPath")
                }
                
                log.info("Loading credentials from: $credentialsPath")
                val credentials = GoogleCredentials.fromStream(FileInputStream(credFile))
                
                val options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build()

                FirebaseApp.initializeApp(options)
                log.info("Firebase initialized successfully")
            }
            
            val firestore = FirestoreClient.getFirestore()
            log.info("Firestore connection established")
            return firestore
            
        } catch (e: FileNotFoundException) {
            log.error("Firebase credentials file not found: ${e.message}")
            throw RuntimeException("Failed to initialize Firebase: credentials file not found at $credentialsPath", e)
        } catch (e: Exception) {
            log.error("Failed to initialize Firebase: ${e.message}", e)
            throw RuntimeException("Failed to initialize Firebase connection", e)
        }
    }

    @Produces
    @ApplicationScoped
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}
