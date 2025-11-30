package com.walkers.poi.service

import com.walkers.poi.model.MediaItem
import com.walkers.poi.model.MediaType
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@ApplicationScoped
class MediaService {
    private val media = ConcurrentHashMap<String, MutableList<MediaItem>>()
    private var counter = 0

    fun upload(poiId: String, type: MediaType, caption: String?): MediaItem {
        val id = "media_${++counter}"
        val item = MediaItem(
            id = id,
            type = type,
            url = "https://storage.example.com/$poiId/$id",
            thumbnailUrl = if (type == MediaType.image) "https://storage.example.com/$poiId/${id}_thumb" else null,
            caption = caption,
            uploadedAt = Instant.now()
        )
        media.computeIfAbsent(poiId) { mutableListOf() }.add(item)
        return item
    }

    fun delete(poiId: String, mediaId: String): Boolean {
        val items = media[poiId] ?: return false
        return items.removeIf { it.id == mediaId }
    }

    fun findByPOI(poiId: String): List<MediaItem> = media[poiId] ?: emptyList()
}
