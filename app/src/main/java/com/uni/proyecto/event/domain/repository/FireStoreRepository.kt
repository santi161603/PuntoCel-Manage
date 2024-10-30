package com.uni.proyecto.event.domain.repository

import com.uni.proyecto.event.domain.model.TiposEvento
import com.uni.proyecto.event.domain.model.UsuarioFirestore

interface FireStoreRepository {
    fun getEventos(onResult: (MutableList<TiposEvento>) -> Unit)
    suspend fun registerUserFirestore(user : UsuarioFirestore, uid : String): Result<Boolean>
    suspend fun addEvent(event: TiposEvento, name: String): Result<Boolean>
}