package com.walkers.poi.repository

import com.google.cloud.firestore.Firestore
import com.walkers.poi.model.MediaItem
import jakarta.enterprise.context.ApplicationScoped
import java.util.concurrent.TimeUnit

@ApplicationScoped
class MediaRepository(private val firestore: Firestore) {

    private val collection = "media"

    fun save(media: MediaItem): MediaItem {
        firestore.collection(collection).document(media.id).set(media).get(10, TimeUnit.SECONDS)
        return media
    }

    fun findById(id: String): MediaItem? {
        val doc = firestore.collection(collection).document(id).get().get(10, TimeUnit.SECONDS)
        return if (doc.exists()) doc.toObject(MediaItem::class.java) else null
    }

    fun findByPoiId(poiId: String): List<MediaItem> {
        return firestore.collection(collection).whereEqualTo("poiId", poiId).get().get(10, TimeUnit.SECONDS)
            .documents.mapNotNull { it.toObject(MediaItem::class.java) }
    }

    fun delete(id: String): Boolean {
        firestore.collection(collection).document(id).delete().get(10, TimeUnit.SECONDS)
        return true
    }
}
