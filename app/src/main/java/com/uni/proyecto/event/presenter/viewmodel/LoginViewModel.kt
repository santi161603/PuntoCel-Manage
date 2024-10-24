package com.uni.proyecto.event.presenter.viewmodel

import androidx.datastore.dataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uni.proyecto.event.data.local.datastore.DataStoreManager
import com.uni.proyecto.event.domain.usecase.GetEventosUseCase
import com.uni.proyecto.event.domain.usecase.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel constructor(private val loginUseCase: LoginUseCase, private val dataStoreManager: DataStoreManager): ViewModel()  {

    private val _uiModel = MutableLiveData<UiModel>()
    val uiModel: LiveData<UiModel> get() = _uiModel

    fun login(email: String, password: String) {

        _uiModel.value = UiModel.Loading

        if(email.isEmpty() || password.isEmpty()){
            _uiModel.value = UiModel.HideLoading
            _uiModel.value = UiModel.LoginError("Por favor ingrese todos los campos")
            return
        }

        viewModelScope.launch (Dispatchers.Main) {
            val result = loginUseCase(email, password)

            if(result.isSuccess){
                saveDataUser(email, password)
                _uiModel.value = UiModel.LoginSuccess
            }else{
                _uiModel.value = UiModel.LoginError( result.exceptionOrNull()?.message ?: "Error Desconocido")
            }

        }

    }

    fun validateUserData() {
        viewModelScope.launch (Dispatchers.Main){
            val email = dataStoreManager.email
            val password = dataStoreManager.password

            // Combina ambos flujos
            email.combine(password) { email, password ->
                Pair(email, password)
            }.collect { (email, password) ->
                if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                    _uiModel.value = UiModel.SessionSuccess
                } else {
                    _uiModel.value = UiModel.SessionNull
                }
            }
        }
    }

    private suspend fun saveDataUser(email: String, password: String) {
        withContext(Dispatchers.IO) {
            dataStoreManager.saveUserData(email, password)
        }
    }

    sealed class UiModel{
        object Loading: UiModel()
        object LoginSuccess: UiModel()
        data class LoginError(val message: String): UiModel()
        object HideLoading: UiModel()
        object SessionSuccess : UiModel()
        object SessionNull : UiModel()
    }
}

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginUseCase,dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}