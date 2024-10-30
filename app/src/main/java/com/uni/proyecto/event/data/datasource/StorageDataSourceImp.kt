package com.uni.proyecto.event.data.datasource

import android.net.Uri
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.uni.proyecto.event.domain.datasource.StorageDataSource
import com.uni.proyecto.event.domain.singles.SingleInstancesFB
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class StorageDataSourceImp constructor(private val storage: FirebaseStorage) : StorageDataSource {
    override suspend fun uploadImage(imageUri: Uri): Result<String> {
        return suspendCancellableCoroutine { continuation ->

            val imageRef =
                SingleInstancesFB.storage.reference.child("images/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")

            // Subir la imagen a Firebase Storage
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    // Obtener la URL de descarga
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        continuation.resume(Result.success(uri.toString()))
                    }.addOnFailureListener { e ->
                        continuation.resume(Result.failure(Exception("Error al obtener la URL de la imagen: ${e.message}")))
                    }
                }
                .addOnFailureListener { exception ->
                    when (exception) {
                        is StorageException -> {
                            val errorMessage = when (exception.errorCode) {
                                StorageException.ERROR_OBJECT_NOT_FOUND -> "El objeto no se encontró en Firebase Storage."
                                StorageException.ERROR_QUOTA_EXCEEDED -> "Se ha excedido la cuota de almacenamiento de Firebase."
                                StorageException.ERROR_NOT_AUTHENTICATED -> "No autenticado. Por favor, inicie sesión."
                                StorageException.ERROR_NOT_AUTHORIZED -> "No tiene permiso para realizar esta acción."
                                else -> "Error desconocido al subir la imagen: ${exception.message}"
                            }
                            continuation.resume(Result.failure(Exception(errorMessage)))
                        }

                        is FirebaseNetworkException -> {
                            continuation.resume(Result.failure(Exception("Error de red. Verifique su conexión a Internet.")))
                        }

                        else -> {
                            continuation.resume(Result.failure(Exception("Error desconocido: ${exception.message}")))
                        }
                    }
                }
        }
    }
}