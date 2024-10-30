package com.uni.proyecto.event.domain.repository

import android.net.Uri

interface StorageRepository {

    suspend fun uploadImage(imageUri: Uri): Result<String>
}