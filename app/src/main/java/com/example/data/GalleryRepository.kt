package com.example.data

import com.example.data.database.AppDatabase
import com.example.data.database.MediaEntity
import com.example.data.database.DirectoryEntity

class GalleryRepository(private val database: AppDatabase) {

    suspend fun getDirectory(path: String): List<ApiMedia>? {
        return try {
            val mediaEntities = database.mediaDao().getMediaByPath(path)
            if (mediaEntities.isEmpty()) return null
            mediaEntities.map { 
                ApiMedia(
                    id = it.id,
                    name = it.name,
                    parentPath = it.parentPath,
                    metadata = ApiMediaMetadata(creationDate = it.creationDate, fileSize = it.fileSize)
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllMedia(): List<ApiMedia>? {
        return try {
            val mediaEntities = database.mediaDao().getAllMedia()
            if (mediaEntities.isEmpty()) return null
            mediaEntities.map { 
                ApiMedia(
                    id = it.id,
                    name = it.name,
                    parentPath = it.parentPath,
                    metadata = ApiMediaMetadata(creationDate = it.creationDate, fileSize = it.fileSize)
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveAllMedia(mediaList: List<ApiMedia>) {
        if (mediaList.isEmpty()) return
        try {
            val entities = mediaList.map { media ->
                val parentPath = if (!media.parentPath.isNullOrEmpty()) {
                    media.parentPath
                } else ""
                val mediaId = if (media.id != null && media.id != 0) {
                    media.id
                } else {
                    ("$parentPath/${media.name}").hashCode()
                }
                val creationDate = media.metadata?.creationDate
                val fileSize = media.metadata?.fileSize

                MediaEntity(
                    id = mediaId,
                    name = media.name,
                    parentPath = parentPath,
                    creationDate = creationDate,
                    fileSize = fileSize
                )
            }
            entities.chunked(500).forEach { chunk ->
                database.mediaDao().insertMedia(chunk)
            }
        } catch (e: Exception) {
            // Prevent fatal crash on SQLite write/disk failure
        }
    }

    suspend fun saveDirectory(path: String, media: List<ApiMedia>) {
        try {
            val entities = media.map {
                val mediaId = if (it.id != null && it.id != 0) {
                    it.id
                } else {
                    ("$path/${it.name}").hashCode()
                }
                MediaEntity(
                    id = mediaId,
                    name = it.name,
                    parentPath = path,
                    creationDate = it.metadata?.creationDate,
                    fileSize = it.metadata?.fileSize
                )
            }
            database.mediaDao().deleteMediaByPath(path)
            entities.chunked(500).forEach { chunk ->
                database.mediaDao().insertMedia(chunk)
            }
            database.directoryDao().insertDirectory(DirectoryEntity(path, path.substringAfterLast('/'), System.currentTimeMillis()))
        } catch (e: Exception) {
            // Prevent fatal crash on SQLite write/disk failure
        }
    }

    suspend fun clearAllCache() {
        try {
            database.mediaDao().clearAllMedia()
            database.directoryDao().clearAllDirectories()
        } catch (e: Exception) {
            // Prevent fatal crash
        }
    }
}
