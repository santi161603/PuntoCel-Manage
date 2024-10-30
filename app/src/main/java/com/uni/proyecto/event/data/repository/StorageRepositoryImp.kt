package com.uni.proyecto.event.data.repository

import android.net.Uri
import com.uni.proyecto.event.domain.datasource.StorageDataSource
import com.uni.proyecto.event.domain.repository.StorageRepository

class StorageRepositoryImp constructor(private val storageDataSource: StorageDataSource) : StorageRepository {
    override suspend fun uploadImage(imageUri: Uri): Result<String> {
       return storageDataSource.uploadImage(imageUri)
    }
}