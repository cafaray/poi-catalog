package com.walkers.poi.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.walkers.poi.model.*
import com.walkers.poi.repository.POIRepository
import jakarta.enterprise.context.ApplicationScoped
import org.jboss.logging.Logger
import java.io.InputStream

@ApplicationScoped
class FileUploadService(
    private val repository: POIRepository,
    private val objectMapper: ObjectMapper
) {
    private val log = Logger.getLogger(FileUploadService::class.java)
    private val validTypes = setOf("monument", "museum", "park", "restaurant", "hotel", "landmark", "historical", "religious", "natural", "other")

    fun uploadPOIsFromFile(inputStream: InputStream): FileUploadResult {
        val errors = mutableListOf<UploadError>()
        var successful = 0
        var failed = 0

        try {
            val fileData = objectMapper.readValue(inputStream, POIFileUpload::class.java)
            
            if (fileData.pois.isEmpty()) {
                throw IllegalArgumentException("File contains no POIs")
            }

            log.info("Processing ${fileData.pois.size} POIs from file")

            fileData.pois.forEach { poi ->
                try {
                    validatePOI(poi)
                    repository.save(poi)
                    successful++
                    log.info("Successfully saved POI: ${poi.id}")
                } catch (e: Exception) {
                    failed++
                    errors.add(UploadError(poi.id, poi.name, e.message ?: "Unknown error"))
                    log.error("Failed to save POI ${poi.id}: ${e.message}")
                }
            }

            log.info("Upload completed: $successful successful, $failed failed")
            
        } catch (e: Exception) {
            log.error("Failed to parse file: ${e.message}")
            throw IllegalArgumentException("Invalid file format: ${e.message}")
        }

        return FileUploadResult(
            totalProcessed = successful + failed,
            successful = successful,
            failed = failed,
            errors = errors
        )
    }

    private fun validatePOI(poi: POIEntity) {
        if (poi.id.isBlank()) throw IllegalArgumentException("POI ID is required")
        if (poi.name.isBlank()) throw IllegalArgumentException("POI name is required")
        if (poi.latitude < -90 || poi.latitude > 90) throw IllegalArgumentException("Invalid latitude: ${poi.latitude}")
        if (poi.longitude < -180 || poi.longitude > 180) throw IllegalArgumentException("Invalid longitude: ${poi.longitude}")
        if (!validTypes.contains(poi.type)) throw IllegalArgumentException("Invalid POI type: ${poi.type}")
        if (poi.createdAt <= 0) throw IllegalArgumentException("Invalid createdAt timestamp")
        if (poi.updatedAt <= 0) throw IllegalArgumentException("Invalid updatedAt timestamp")
    }
}
