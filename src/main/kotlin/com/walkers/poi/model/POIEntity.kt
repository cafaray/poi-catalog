package com.walkers.poi.model

data class POIEntity(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: String = "",
    val description: String? = null,
    val address: String? = null,
    val tags: List<String>? = null,
    val mediaIds: List<String>? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val verified: Boolean = false,
    val source: String = "manual_curation"
)
