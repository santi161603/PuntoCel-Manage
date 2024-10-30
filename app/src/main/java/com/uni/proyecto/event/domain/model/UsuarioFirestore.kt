package com.uni.proyecto.event.domain.model

data class UsuarioFirestore(
    val nombre: String = "",
    val apellidos: String = "",
    val cedula: String = "",
    val celular: String = "",
    val tipoUsuario: String = "",
    val nacimiento: String = "",
    val email: String = ""
)
