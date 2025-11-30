package com.walkers.poi.resource

import com.walkers.poi.model.*
import com.walkers.poi.service.MediaService
import com.walkers.poi.service.POIService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.multipart.FileUpload

@Path("/v1/pois/{poiId}/media")
@Produces(MediaType.APPLICATION_JSON)
class MediaResource(
    private val mediaService: MediaService,
    private val poiService: POIService
) {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun upload(
        @PathParam("poiId") poiId: String,
        @FormParam("file") file: FileUpload?,
        @FormParam("type") type: com.walkers.poi.model.MediaType?,
        @FormParam("caption") caption: String?
    ): Response {
        if (poiService.findById(poiId) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ErrorResponse(error = "POI not found", code = "NOT_FOUND"))
                .build()
        }

        val mediaType = type ?: com.walkers.poi.model.MediaType.image
        val item = mediaService.upload(poiId, mediaType, caption)
        
        return Response.status(Response.Status.CREATED)
            .entity(SuccessResponse(data = item))
            .build()
    }

    @DELETE
    @Path("/{mediaId}")
    fun delete(
        @PathParam("poiId") poiId: String,
        @PathParam("mediaId") mediaId: String
    ): Response {
        if (poiService.findById(poiId) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ErrorResponse(error = "POI not found", code = "NOT_FOUND"))
                .build()
        }

        if (!mediaService.delete(poiId, mediaId)) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ErrorResponse(error = "Media not found", code = "NOT_FOUND"))
                .build()
        }

        return Response.ok(SuccessResponse(data = null)).build()
    }
}
