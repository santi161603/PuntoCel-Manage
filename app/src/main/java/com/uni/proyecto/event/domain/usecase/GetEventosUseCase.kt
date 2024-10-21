package com.uni.proyecto.event.domain.usecase

import com.uni.proyecto.event.domain.model.TiposEvento
import com.uni.proyecto.event.domain.repository.FireStoreRepository

class GetEventosUseCase constructor(private val fireStoreRepository: FireStoreRepository) {

    fun getEventos(onResult: (MutableList<TiposEvento>) -> Unit){
        fireStoreRepository.getEventos(onResult)
    }

}