package com.walkers.poi.resource

import com.walkers.poi.model.ErrorResponse
import com.walkers.poi.model.SuccessResponse
import com.walkers.poi.service.FileUploadService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload

@Path("/v1/pois/upload-file")
@Produces(MediaType.APPLICATION_JSON)
class FileUploadResource(private val fileUploadService: FileUploadService) {

    private val log = Logger.getLogger(FileUploadResource::class.java)

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun uploadFile(@FormParam("file") file: FileUpload?): Response {
        if (file == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse(error = "File is required", code = "FILE_REQUIRED"))
                .build()
        }

        if (!file.fileName().endsWith(".json")) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse(error = "Only JSON files are allowed", code = "INVALID_FILE_TYPE"))
                .build()
        }

        if (file.size() > 10 * 1024 * 1024) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse(error = "File size exceeds 10MB limit", code = "FILE_TOO_LARGE"))
                .build()
        }

        return try {
            log.info("Processing file upload: ${file.fileName()}")
            val result = file.uploadedFile().toFile().inputStream().use { inputStream ->
                fileUploadService.uploadPOIsFromFile(inputStream)
            }

            if (result.failed > 0) {
                Response.status(Response.Status.PARTIAL_CONTENT)
                    .entity(SuccessResponse(data = result))
                    .build()
            } else {
                Response.status(Response.Status.CREATED)
                    .entity(SuccessResponse(data = result))
                    .build()
            }
        } catch (e: IllegalArgumentException) {
            log.error("Validation error: ${e.message}")
            Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse(error = e.message ?: "Validation failed", code = "VALIDATION_ERROR"))
                .build()
        } catch (e: Exception) {
            log.error("Upload failed: ${e.message}", e)
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse(error = "File upload failed", code = "UPLOAD_ERROR"))
                .build()
        }
    }
}
