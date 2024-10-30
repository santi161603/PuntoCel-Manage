package com.uni.proyecto.event.domain.usecase

import com.uni.proyecto.event.domain.model.TiposEvento
import com.uni.proyecto.event.domain.repository.FireStoreRepository

class AddEventFirestoreUseCase constructor(private val repository: FireStoreRepository) {

    suspend fun addEvent(event: TiposEvento, name : String): Result<Boolean> {
        return repository.addEvent(event, name)
    }
}