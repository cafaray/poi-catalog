package com.walkers.poi.resource

import com.walkers.poi.model.*
import com.walkers.poi.service.POIService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/v1/pois")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class POIResource(private val poiService: POIService) {

    @POST
    fun create(request: POICreate): Response {
        val poi = poiService.create(request)
        return Response.status(Response.Status.CREATED)
            .entity(SuccessResponse(data = poi))
            .build()
    }

    @GET
    fun list(
        @QueryParam("page") @DefaultValue("1") page: Int,
        @QueryParam("limit") @DefaultValue("20") limit: Int,
        @QueryParam("type") type: POIType?,
        @QueryParam("verified") verified: Boolean?
    ): Response {
        val result = poiService.findAll(page, limit, type, verified)
        return Response.ok(SuccessResponse(data = result)).build()
    }

    @GET
    @Path("/{poiId}")
    fun get(@PathParam("poiId") poiId: String): Response {
        val poi = poiService.findById(poiId)
            ?: return Response.status(Response.Status.NOT_FOUND)
                .entity(ErrorResponse(error = "POI not found", code = "NOT_FOUND"))
                .build()
        return Response.ok(SuccessResponse(data = poi)).build()
    }

    @PUT
    @Path("/{poiId}")
    fun update(@PathParam("poiId") poiId: String, request: POIUpdate): Response {
        val poi = poiService.update(poiId, request)
            ?: return Response.status(Response.Status.NOT_FOUND)
                .entity(ErrorResponse(error = "POI not found", code = "NOT_FOUND"))
                .build()
        return Response.ok(SuccessResponse(data = poi)).build()
    }

    @DELETE
    @Path("/{poiId}")
    fun delete(@PathParam("poiId") poiId: String): Response {
        if (!poiService.delete(poiId)) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ErrorResponse(error = "POI not found", code = "NOT_FOUND"))
                .build()
        }
        return Response.ok(SuccessResponse(data = null)).build()
    }

    @POST
    @Path("/bulk-import")
    fun bulkImport(request: BulkPOICreate): Response {
        val result = poiService.bulkImport(request)
        return Response.status(Response.Status.CREATED)
            .entity(SuccessResponse(data = result))
            .build()
    }

    @GET
    @Path("/search")
    fun search(
        @QueryParam("page") @DefaultValue("1") page: Int,
        @QueryParam("limit") @DefaultValue("20") limit: Int,
        @QueryParam("lat") lat: Double?,
        @QueryParam("lng") lng: Double?,
        @QueryParam("radius") @DefaultValue("1000") radius: Int?,
        @QueryParam("q") q: String?,
        @QueryParam("tags") tags: List<String>?
    ): Response {
        val result = poiService.search(page, limit, lat, lng, radius, q, tags)
        return Response.ok(SuccessResponse(data = result)).build()
    }
}
