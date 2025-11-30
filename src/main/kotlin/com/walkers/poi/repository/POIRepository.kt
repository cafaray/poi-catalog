package com.walkers.poi.repository

import com.google.cloud.firestore.Firestore
import com.walkers.poi.model.POIEntity
import jakarta.enterprise.context.ApplicationScoped
import java.util.concurrent.TimeUnit

@ApplicationScoped
class POIRepository(private val firestore: Firestore) {

    private val collection = "pois"

    fun save(poi: POIEntity): POIEntity {
        firestore.collection(collection).document(poi.id).set(poi).get(10, TimeUnit.SECONDS)
        return poi
    }

    fun findById(id: String): POIEntity? {
        val doc = firestore.collection(collection).document(id).get().get(10, TimeUnit.SECONDS)
        return if (doc.exists()) doc.toObject(POIEntity::class.java) else null
    }

    fun findAll(): List<POIEntity> {
        return firestore.collection(collection).get().get(10, TimeUnit.SECONDS)
            .documents.mapNotNull { it.toObject(POIEntity::class.java) }
    }

    fun delete(id: String): Boolean {
        firestore.collection(collection).document(id).delete().get(10, TimeUnit.SECONDS)
        return true
    }

    fun findByType(type: String): List<POIEntity> {
        return firestore.collection(collection).whereEqualTo("type", type).get().get(10, TimeUnit.SECONDS)
            .documents.mapNotNull { it.toObject(POIEntity::class.java) }
    }

    fun search(query: String): List<POIEntity> {
        return findAll().filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.description?.contains(query, ignoreCase = true) == true 
        }
    }
}
