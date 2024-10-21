package com.uni.proyecto.event.data.repository

import com.uni.proyecto.event.domain.datasource.FireStoreDataSource
import com.uni.proyecto.event.domain.model.TiposEvento
import com.uni.proyecto.event.domain.repository.FireStoreRepository

class FireStoreRepositoryImp constructor(private  val fireStoreDataSource: FireStoreDataSource): FireStoreRepository {
    override fun getEventos(onResult: (MutableList<TiposEvento>) -> Unit) {
        fireStoreDataSource.getEventos(onResult)
    }
}