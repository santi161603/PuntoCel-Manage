package com.uni.proyecto.event.domain.repository

interface AuthenticationRepository {

   suspend fun login(email: String, password: String) : Result<Boolean>

}