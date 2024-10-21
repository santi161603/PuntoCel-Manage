package com.uni.proyecto.event.domain.repository

import com.uni.proyecto.event.domain.model.TiposEvento

interface FireStoreRepository {
    fun getEventos(onResult: (MutableList<TiposEvento>) -> Unit)
}