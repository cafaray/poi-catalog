package com.walkers.poi.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class POICreate(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: POIType,
    val description: String? = null,
    val address: String? = null,
    val tags: List<String>? = null
)

data class POIUpdate(
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val type: POIType? = null,
    val description: String? = null,
    val address: String? = null,
    val tags: List<String>? = null
)

data class POIResponse(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: String,
    val description: String? = null,
    val address: String? = null,
    val tags: List<String>? = null,
    val media: List<MediaItem>? = null,
    val metadata: POIMetadata
)

data class POIMetadata(
    @JsonProperty("created_at") val createdAt: Instant,
    @JsonProperty("updated_at") val updatedAt: Instant,
    val verified: Boolean = false,
    val source: Source = Source.manual_curation
)

data class MediaItem(
    val id: String,
    val type: MediaType,
    val url: String,
    @JsonProperty("thumbnail_url") val thumbnailUrl: String? = null,
    val caption: String? = null,
    @JsonProperty("file_size") val fileSize: Long? = null,
    val dimensions: Dimensions? = null,
    @JsonProperty("uploaded_at") val uploadedAt: Instant
)

data class Dimensions(val width: Int, val height: Int)

data class BulkPOICreate(val pois: List<POICreate>)

data class BulkImportResult(
    val created: Int,
    val failed: Int,
    val results: List<ImportResult>
)

data class ImportResult(
    val name: String,
    val status: ImportStatus,
    val id: String? = null,
    val error: String? = null
)

data class PaginatedPOIResponse(
    val pois: List<POIResponse>,
    val pagination: Pagination
)

data class Pagination(
    val total: Int,
    val page: Int,
    val limit: Int,
    @JsonProperty("total_pages") val totalPages: Int,
    @JsonProperty("has_next") val hasNext: Boolean,
    @JsonProperty("has_prev") val hasPrev: Boolean
)

data class SuccessResponse<T>(
    val success: Boolean = true,
    val data: T,
    val timestamp: Instant = Instant.now()
)

data class ErrorResponse(
    val success: Boolean = false,
    val error: String,
    val code: String,
    val timestamp: Instant = Instant.now(),
    @JsonProperty("request_id") val requestId: String? = null
)

enum class POIType {
    monument, museum, park, restaurant, hotel, landmark, historical, religious, natural, other
}

enum class MediaType { image, video }

enum class Source { manual_curation, bulk_import, api_sync }

enum class ImportStatus { created, failed }
