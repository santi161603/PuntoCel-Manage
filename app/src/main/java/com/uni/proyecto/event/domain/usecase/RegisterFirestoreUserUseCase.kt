package com.uni.proyecto.event.domain.usecase

import com.uni.proyecto.event.domain.model.UsuarioFirestore
import com.uni.proyecto.event.domain.repository.FireStoreRepository

class RegisterFirestoreUserUseCase constructor(private val fireStoreRepository: FireStoreRepository) {

    suspend fun registerUserFirestore(user : UsuarioFirestore, uid : String) : Result<Boolean> {
       return fireStoreRepository.registerUserFirestore(user, uid)
    }
}