package com.walkers.poi.model

data class POIFileUpload(
    val pois: List<POIEntity>
)

data class FileUploadResult(
    val totalProcessed: Int,
    val successful: Int,
    val failed: Int,
    val errors: List<UploadError>
)

data class UploadError(
    val poiId: String,
    val poiName: String,
    val error: String
)
