package com.uni.proyecto.event.data.datasource

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.uni.proyecto.event.domain.datasource.FireStoreDataSource
import com.uni.proyecto.event.domain.model.TiposEvento
import com.uni.proyecto.event.domain.model.UsuarioFirestore
import com.uni.proyecto.event.domain.singles.SingleInstancesFB
import com.uni.proyecto.event.presenter.viewmodel.HomeViewModel.UiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FireStoreDataSourceImp constructor(private val firestore: FirebaseFirestore, private var listenerRegistration: ListenerRegistration? = null): FireStoreDataSource{

    override fun getEventos(onResult: (MutableList<TiposEvento>) -> Unit){

        val eventosList = mutableListOf<TiposEvento>()

            listenerRegistration = firestore.collection("TiposEventos")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.w("Firestore", "Error escuchando cambios: ", error)
                        onResult(eventosList)
                        return@addSnapshotListener
                    }else {

                        eventosList.clear()

                        snapshots?.let {
                            for (document in it) {

                                val documentoId = document.id
                                val evento = document.toObject(TiposEvento::class.java)

                                evento.nombre = documentoId
                                eventosList.add(evento)
                            }
                        }
                        onResult(eventosList)
                    }
                }
    }

    override suspend fun registerUserFirestore(user: UsuarioFirestore, uid: String): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("Usuarios").document(uid).set(user)
                .addOnSuccessListener {
                    continuation.resume(Result.success(true))
                }
                .addOnFailureListener { exception ->
                    when (exception) {
                        is FirebaseFirestoreException -> {
                            val errorMessage = when (exception.code) {
                                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Permiso denegado para guardar los datos."
                                FirebaseFirestoreException.Code.UNAVAILABLE -> "Servicio no disponible. Intente de nuevo más tarde."
                                FirebaseFirestoreException.Code.ALREADY_EXISTS -> "El documento ya existe."
                                else -> "Error desconocido al guardar los datos: ${exception.message}"
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

    override suspend fun addEvent(event: TiposEvento, name: String): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("TiposEventos").document(name).set(event)
                .addOnSuccessListener {
                    continuation.resume(Result.success(true))
                }
                .addOnFailureListener { exception ->
                    when (exception) {
                        is FirebaseFirestoreException -> {
                            val errorMessage = when (exception.code) {
                                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Permiso denegado para guardar los datos."
                                FirebaseFirestoreException.Code.UNAVAILABLE -> "Servicio no disponible. Intente de nuevo más tarde."
                                FirebaseFirestoreException.Code.ALREADY_EXISTS -> "El documento ya existe."
                                else -> "Error desconocido al guardar los datos: ${exception.message}"
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