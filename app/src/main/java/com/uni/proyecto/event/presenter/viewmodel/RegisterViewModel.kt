package com.uni.proyecto.event.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uni.proyecto.event.domain.usecase.RegisterUserUseCase
import com.uni.proyecto.event.presenter.viewmodel.LoginViewModel.UiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel constructor(private val registerUserUseCase: RegisterUserUseCase) : ViewModel() {

    private val _uiModel = MutableLiveData<UiModel>()
    val uiModel: LiveData<UiModel> get() = _uiModel

    fun register(email: String, password: String) {
        _uiModel.value = UiModel.Loading

        if(email.isEmpty() || password.isEmpty()){
            _uiModel.value = UiModel.HideLoading
            _uiModel.value = UiModel.RegisterError("Por favor ingrese todos los campos")
            return
        }
        if(password.length < 6){
            _uiModel.value = UiModel.HideLoading
            _uiModel.value = UiModel.RegisterError("La contraseña debe tener al menos 6 caracteres")
            return
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                _uiModel.value = UiModel.HideLoading
                _uiModel.value = UiModel.RegisterError("El email no es valido")
                return
        }
      viewModelScope.launch(Dispatchers.IO) {
          val result = registerUserUseCase(email, password)

          withContext(Dispatchers.Main) {
              if (result.isSuccess) {
                  _uiModel.value = UiModel.RegisterUserFirestore
                  _uiModel.value = UiModel.HideLoading // O cualquier otro estado de éxito
              } else {
                  // Manejo del error (puedes crear un estado de error)
                  _uiModel.value = UiModel.HideLoading // Ocultar carga
                  _uiModel.value = UiModel.RegisterError(result.exceptionOrNull()?.message ?: "Error al registrar")
              }
          }
      }

    }

    sealed class UiModel{
        class RegisterError(val message: String) : UiModel()
        object Loading: UiModel()
        object HideLoading: UiModel()
        object RegisterUserFirestore : UiModel() {

        }
    }
}

class RegisterViewModelFactory(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(registerUserUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}