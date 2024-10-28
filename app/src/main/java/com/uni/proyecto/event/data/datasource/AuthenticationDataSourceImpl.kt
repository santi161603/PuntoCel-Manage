package com.uni.proyecto.event.data.datasource

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.uni.proyecto.event.domain.datasource.AuthenticationDataSource
import com.uni.proyecto.event.domain.exceptions.AuthException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthenticationDataSourceImpl constructor(private val firebaseAuth: FirebaseAuth) :
    AuthenticationDataSource {

    override suspend fun login(email: String, password: String): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    continuation.resume(Result.success(true))
                }
                .addOnFailureListener { exception ->
                    when (exception) {
                        is FirebaseAuthException -> {

                            var errorMessage = ""

                            when (exception.errorCode) {
                                "ERROR_INVALID_CREDENTIAL" -> errorMessage =
                                    "Credenciales inválidas."

                                "ERROR_USER_NOT_FOUND" -> errorMessage = "Usuario no encontrado."
                                "ERROR_WRONG_PASSWORD" -> errorMessage = "Contraseña incorrecta."

                                else -> {
                                    continuation.resume(Result.failure(AuthException("Error desconocido" + exception.message)))
                                }
                            }
                            continuation.resume(Result.failure(AuthException(errorMessage)))
                        }

                        is FirebaseNetworkException -> {
                            continuation.resume(Result.failure(AuthException("Error de red. Verifique su conexión a Internet.")))
                        }

                        else -> {
                            // Otros errores (generalmente inesperados)
                            continuation.resume(Result.failure(AuthException("Error desconocido" + exception.message)))
                        }
                    }

                }

            // Si la coroutine es cancelada, cancelar el intento de autenticación
            continuation.invokeOnCancellation {
                firebaseAuth.signOut()
            }
        }
    }

    override suspend fun registerUser(email: String, password: String): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    continuation.resume(Result.success(true)) // Registro exitoso
                }
                .addOnFailureListener { exception ->
                    val errorMessage = when (exception) {
                        is FirebaseAuthException -> {
                            when (exception.errorCode) {
                                "ERROR_EMAIL_ALREADY_IN_USE" -> "El correo electrónico ya está en uso."
                                "ERROR_INVALID_EMAIL" -> "Formato de correo electrónico inválido."
                                "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado débil. Debe tener al menos 6 caracteres."
                                else -> "Error desconocido en el registro: ${exception.message}"
                            }
                        }

                        is FirebaseNetworkException -> "Error de red. Verifique su conexión a Internet."
                        else -> "Error desconocido en el registro: ${exception.message}"
                    }

                    continuation.resume(Result.failure(AuthException(errorMessage)))
                }

            // Cancelar la operación de Firebase si el coroutine se cancela
            continuation.invokeOnCancellation {
                // Aquí puedes agregar lógica adicional si es necesario
            }
        }
    }
}