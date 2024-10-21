package com.uni.proyecto.event.data.datasource

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.uni.proyecto.event.domain.datasource.FireStoreDataSource
import com.uni.proyecto.event.domain.model.TiposEvento
import com.uni.proyecto.event.domain.singles.SingleInstancesFB
import com.uni.proyecto.event.presenter.viewmodel.HomeViewModel.UiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FireStoreDataSourceImp constructor(private val firestore: FirebaseFirestore, private var listenerRegistration: ListenerRegistration?): FireStoreDataSource{

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

}