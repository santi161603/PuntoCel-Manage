package com.uni.proyecto.event.domain.usecase

import android.net.Uri
import com.uni.proyecto.event.domain.repository.StorageRepository

class UpdateImageUseCase constructor(private val storageRepository: StorageRepository) {

    suspend fun updateImageStorege(imageUri : Uri) : Result<String> {
        return storageRepository.uploadImage(imageUri)
    }

}