package com.uni.proyecto.event.presenter.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uni.proyecto.event.data.local.datastore.DataStoreManager
import com.uni.proyecto.event.domain.model.TiposEvento
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.firestore
import com.uni.proyecto.event.domain.usecase.AddEventFirestoreUseCase
import com.uni.proyecto.event.domain.usecase.LoginUseCase
import com.uni.proyecto.event.domain.usecase.UpdateImageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageEventsViewModel constructor(private val updateImageUseCase: UpdateImageUseCase, private val addEventFirestoreUseCase: AddEventFirestoreUseCase) : ViewModel() {

    private val _uiModel = MutableLiveData<UiModel>()
    val uiModel: LiveData<UiModel> get() = _uiModel

    fun validData(
        nombreEvento: String,
        costoMinimo: String,
        descripcion: String,
        miniDescripcion: String,
        imageUri: Uri?,
        listaServicios: String
    ) {
        _uiModel.value = UiModel.Loading

        if (nombreEvento.isEmpty() ||
            costoMinimo.isEmpty() ||
            descripcion.isEmpty() ||
            miniDescripcion.isEmpty() || miniDescripcion.length < 10 ||
            imageUri == null ||
            listaServicios.isEmpty() ||
            costoMinimo.isEmpty()
        ) {
            _uiModel.value = UiModel.HideLoading
            _uiModel.value = UiModel.Error("Por favor, completa todos los campos correctamente")
            return
        }
        uploadImageToFirebase(imageUri) { imageUrl ->

            val listaServiciosArray = convertirArray(listaServicios)

            addEvent(
                nombreEvento,
                costoMinimo,
                descripcion,
                miniDescripcion,
                imageUrl,
                listaServiciosArray
            )

        }

    }

    private fun convertirArray(listaServicios: String): Array<String> {
        return listaServicios.split(",").map { it.trim() }.toTypedArray()
    }

    private fun addEvent(
        nombreEvento: String,
        costoMinimo: String,
        descripcion: String,
        miniDescripcion: String,
        imageUrl: String,
        listaServicios: Array<String>
    ) {

        // Crear una instancia de TiposEvento con los datos
        val evento = TiposEvento(
            nombre = null,  // El nombre será el ID del documento
            descripcion = descripcion,
            serviciosIncluidos = listaServicios.toList(),
            costo_minimo = costoMinimo,
            image = imageUrl,
            miniDescripcion = miniDescripcion
        )

        viewModelScope.launch(Dispatchers.Main) {
            val result = addEventFirestoreUseCase.addEvent(evento, nombreEvento)
            if (result.isSuccess) {
                _uiModel.value = UiModel.HideLoading
                _uiModel.value = UiModel.EventoAnadido
            }
            else {
                _uiModel.value = UiModel.HideLoading
                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                _uiModel.value = UiModel.Error(errorMessage)
            }
        }

    }

    private fun uploadImageToFirebase(imageUri: Uri, onSuccess: (String) -> Unit) {

        viewModelScope.launch(Dispatchers.Main) {
            val result = updateImageUseCase.updateImageStorege(imageUri)

            if (result.isSuccess) {
                val imageUrl = result.getOrNull()
                if (imageUrl != null) {
                    onSuccess(imageUrl)
                } else {
                    _uiModel.value = UiModel.HideLoading
                    _uiModel.value = UiModel.Error("Error al obtener la URL de la imagen")
                }
            }
            else {
                _uiModel.value = UiModel.HideLoading
                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                _uiModel.value = UiModel.Error(errorMessage)
            }

        }

    }

    sealed class UiModel {
        object Loading : UiModel()
        data class EventosObtenidos(val eventosList: List<TiposEvento>) : UiModel()
        class Error(val mss: String) : UiModel()
        object HideLoading : UiModel()
        object EventoAnadido : UiModel()
    }
}
class ManageEventsModelFactory(
    private val updateImageUseCase: UpdateImageUseCase,
    private val addEventFirestoreUseCase: AddEventFirestoreUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageEventsViewModel::class.java)) {
            return ManageEventsViewModel(updateImageUseCase,addEventFirestoreUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}