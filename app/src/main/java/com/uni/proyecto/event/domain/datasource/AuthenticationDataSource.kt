package com.uni.proyecto.event.domain.datasource

interface AuthenticationDataSource {

        suspend fun login(email: String, password: String): Result<Boolean>
}