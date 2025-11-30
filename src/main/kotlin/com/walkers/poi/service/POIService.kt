package com.walkers.poi.service

import com.walkers.poi.model.*
import com.walkers.poi.repository.POIRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class POIService(private val repository: POIRepository) {

    fun create(request: POICreate): POIResponse {
        val id = "poi_${UUID.randomUUID()}"
        val now = Instant.now()
        val entity = POIEntity(
            id = id,
            name = request.name,
            latitude = request.latitude,
            longitude = request.longitude,
            type = request.type.name,
            description = request.description,
            address = request.address,
            tags = request.tags,
            createdAt = now.toEpochMilli(),
            updatedAt = now.toEpochMilli()
        )
        repository.save(entity)
        return toResponse(entity)
    }

    fun findById(id: String): POIResponse? = repository.findById(id)?.let { toResponse(it) }

    fun findAll(page: Int, limit: Int, type: POIType?, verified: Boolean?): PaginatedPOIResponse {
        val all = if (type != null) repository.findByType(type.name) else repository.findAll()
        val filtered = all.filter { entity ->
            verified == null || entity.verified == verified
        }.map { toResponse(it) }
        
        val total = filtered.size
        val start = (page - 1) * limit
        val end = minOf(start + limit, total)
        val paginated = if (start < total) filtered.subList(start, end) else emptyList()
        
        return PaginatedPOIResponse(
            pois = paginated,
            pagination = Pagination(
                total = total,
                page = page,
                limit = limit,
                totalPages = (total + limit - 1) / limit,
                hasNext = end < total,
                hasPrev = page > 1
            )
        )
    }

    fun update(id: String, request: POIUpdate): POIResponse? {
        val existing = repository.findById(id) ?: return null
        val updated = existing.copy(
            name = request.name ?: existing.name,
            latitude = request.latitude ?: existing.latitude,
            longitude = request.longitude ?: existing.longitude,
            type = request.type?.name ?: existing.type,
            description = request.description ?: existing.description,
            address = request.address ?: existing.address,
            tags = request.tags ?: existing.tags,
            updatedAt = Instant.now().toEpochMilli()
        )
        repository.save(updated)
        return toResponse(updated)
    }

    fun delete(id: String): Boolean = repository.delete(id)

    fun bulkImport(request: BulkPOICreate): BulkImportResult {
        val results = request.pois.map { poi ->
            try {
                val created = create(poi)
                ImportResult(name = poi.name, status = ImportStatus.created, id = created.id)
            } catch (e: Exception) {
                ImportResult(name = poi.name, status = ImportStatus.failed, error = e.message)
            }
        }
        return BulkImportResult(
            created = results.count { it.status == ImportStatus.created },
            failed = results.count { it.status == ImportStatus.failed },
            results = results
        )
    }

    fun search(page: Int, limit: Int, lat: Double?, lng: Double?, radius: Int?, q: String?, tags: List<String>?): PaginatedPOIResponse {
        val all = if (q != null) repository.search(q) else repository.findAll()
        val filtered = all.filter { entity ->
            tags == null || tags.isEmpty() || entity.tags?.any { it in tags } == true
        }.map { toResponse(it) }
        
        val total = filtered.size
        val start = (page - 1) * limit
        val end = minOf(start + limit, total)
        val paginated = if (start < total) filtered.subList(start, end) else emptyList()
        
        return PaginatedPOIResponse(
            pois = paginated,
            pagination = Pagination(
                total = total,
                page = page,
                limit = limit,
                totalPages = (total + limit - 1) / limit,
                hasNext = end < total,
                hasPrev = page > 1
            )
        )
    }

    private fun toResponse(entity: POIEntity) = POIResponse(
        id = entity.id,
        name = entity.name,
        latitude = entity.latitude,
        longitude = entity.longitude,
        type = entity.type,
        description = entity.description,
        address = entity.address,
        tags = entity.tags,
        media = emptyList(),
        metadata = POIMetadata(
            createdAt = Instant.ofEpochMilli(entity.createdAt),
            updatedAt = Instant.ofEpochMilli(entity.updatedAt),
            verified = entity.verified,
            source = Source.valueOf(entity.source)
        )
    )
}
