package com.uni.proyecto.event.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uni.proyecto.event.domain.model.UsuarioFirestore
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.auth
import com.uni.proyecto.event.domain.usecase.RegisterFirestoreUserUseCase
import com.uni.proyecto.event.domain.usecase.RegisterUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val registerFirestore: RegisterFirestoreUserUseCase
) :
    ViewModel() {

    private val _uiModel = MutableLiveData<UiModel>()
    val uiModel: LiveData<UiModel> get() = _uiModel

    fun register(
        email: String,
        password: String,
        repetirPassword: String,
        nombre: String,
        apellidos: String,
        cedula: String,
        celular: String,
        nacimiento: String
    ) {
        _uiModel.value = UiModel.Loading

        // Validar campos obligatorios
        when (val fieldValidation = validateFields(
            email,
            password,
            repetirPassword,
            nombre,
            apellidos,
            cedula,
            celular,
            nacimiento
        )) {
            is ValidationResult.Error -> {
                _uiModel.value = UiModel.HideLoading
                _uiModel.value = UiModel.RegisterError(fieldValidation.message)
                return
            }

            ValidationResult.Success -> {} // No hacer nada si es exitoso
        }

        // Validar email
        when (val emailValidation = validateEmail(email)) {
            is ValidationResult.Error -> {
                _uiModel.value = UiModel.HideLoading
                _uiModel.value = UiModel.RegisterError(emailValidation.message)
                return
            }

            ValidationResult.Success -> {} // No hacer nada si es exitoso
        }

        // Validar contraseña
        when (val passwordValidation = validatePassword(password, repetirPassword)) {
            is ValidationResult.Error -> {
                _uiModel.value = UiModel.HideLoading
                _uiModel.value = UiModel.RegisterError(passwordValidation.message)
                return
            }

            ValidationResult.Success -> {} // No hacer nada si es exitoso
        }

        viewModelScope.launch(Dispatchers.Main) {
            val result = registerUserUseCase(email, password)
            if (result.isSuccess) {
                registerFirestore(email, nombre, apellidos, cedula, celular, nacimiento)
            } else {
                // Manejo del error (puedes crear un estado de error)
                _uiModel.value = UiModel.HideLoading // Ocultar carga
                _uiModel.value = UiModel.RegisterError(
                    result.exceptionOrNull()?.message ?: "Error al registrar"
                )
            }
        }

    }

    fun registerFirestore(
        email: String,
        nombre: String,
        apellidos: String,
        cedula: String,
        celular: String,
        nacimiento: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {

            val usuario = UsuarioFirestore(
                nombre,
                apellidos,
                cedula,
                celular,
                "Cliente",
                nacimiento,
                email
            )

            val uid = auth.currentUser?.uid ?: ""
            val result = registerFirestore.registerUserFirestore(usuario, uid)

            if (result.isSuccess) {
                _uiModel.value = UiModel.HideLoading
                _uiModel.value = UiModel.Successvalid
            } else {
                _uiModel.value = UiModel.HideLoading
                _uiModel.value = UiModel.RegisterError(
                    result.exceptionOrNull()?.message ?: "Error al registrar"
                )
            }
        }
    }

    private fun validateFields(
        email: String,
        password: String,
        repetirPassword: String,
        nombre: String,
        apellidos: String,
        cedula: String,
        celular: String,
        nacimiento: String
    ): ValidationResult {
        if (email.isEmpty() || password.isEmpty() || repetirPassword.isEmpty() ||
            nombre.isEmpty() || apellidos.isEmpty() || cedula.isEmpty() || celular.isEmpty() || nacimiento.isEmpty()
        ) {
            return ValidationResult.Error("Por favor ingrese todos los campos")
        }
        // Validar que el número de celular tenga 10 dígitos y comience con 3
        if (!celular.matches(Regex("^3\\d{9}$"))) {
            return ValidationResult.Error("Ingrese un número de celular válido")
        }
        return ValidationResult.Success
    }

    private fun validateEmail(email: String): ValidationResult {
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ValidationResult.Error("El email no es válido")
        } else ValidationResult.Success
    }

    private fun validatePassword(password: String, repetirPassword: String): ValidationResult {
        if (password.length < 6) {
            return ValidationResult.Error("La contraseña debe tener al menos 6 caracteres")
        }
        if (password != repetirPassword) {
            return ValidationResult.Error("Las contraseñas no coinciden")
        }
        return ValidationResult.Success
    }


    sealed class UiModel {
        class RegisterError(val message: String) : UiModel()
        object Successvalid : UiModel()
        object Loading : UiModel()
        object HideLoading : UiModel()
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }

}

class RegisterViewModelFactory(
    private val registerUserUseCase: RegisterUserUseCase,
    private val registerFirestore: RegisterFirestoreUserUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(registerUserUseCase, registerFirestore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}