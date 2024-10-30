package com.uni.proyecto.event.domain.datasource

import android.net.Uri

interface StorageDataSource {

    suspend fun uploadImage(imageUri: Uri): Result<String>
}