package com.uni.proyecto.event.data.repository

import com.uni.proyecto.event.domain.datasource.AuthenticationDataSource
import com.uni.proyecto.event.domain.repository.AuthenticationRepository

class AuthenticationRepositoryImpl constructor(private val authenticationDataSource: AuthenticationDataSource) : AuthenticationRepository {

    override suspend fun login(email: String, password: String): Result<Boolean> {
        return authenticationDataSource.login(email, password)
    }

    override suspend fun registerUser(email: String, password: String): Result<Boolean> {
        return authenticationDataSource.registerUser(email, password)
    }
}