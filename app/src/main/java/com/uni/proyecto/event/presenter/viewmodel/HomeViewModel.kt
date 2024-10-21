package com.uni.proyecto.event.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uni.proyecto.event.domain.model.TiposEvento
import com.uni.proyecto.event.domain.usecase.GetEventosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel constructor(private val getEventosUseCase: GetEventosUseCase) : ViewModel() {

    private val _uiModel = MutableLiveData<UiModel>()
    val uiModel: LiveData<UiModel> get() = _uiModel

    fun getTiposEventos(){
        viewModelScope.launch(Dispatchers.Main) {
            _uiModel.value = UiModel.Loading
            getEventosUseCase.getEventos{ listaTiposEventos ->
            if(listaTiposEventos.isEmpty()){
                _uiModel.value = UiModel.HideLoading
                _uiModel.value = UiModel.EventosNoObtenidos
            }else{
                _uiModel.value = UiModel.HideLoading
                _uiModel.value = UiModel.EventosObtenidos(listaTiposEventos)
            }
            }
        }
    }

    sealed class  UiModel {
        data class EventosObtenidos(val eventosList: MutableList<TiposEvento>) : UiModel()
        object Loading: UiModel()
        object HideLoading: UiModel()
        object EventosNoObtenidos : UiModel()

    }

}

class HomeViewModelFactory(
    private val getEventosUseCase: GetEventosUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(getEventosUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}