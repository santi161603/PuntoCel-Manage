package com.uni.proyecto.event.domain.datasource

import com.uni.proyecto.event.domain.model.TiposEvento

interface FireStoreDataSource {
    fun getEventos(onResult: (MutableList<TiposEvento>) -> Unit)
}