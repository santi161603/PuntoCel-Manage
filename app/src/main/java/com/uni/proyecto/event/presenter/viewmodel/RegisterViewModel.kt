package com.uni.proyecto.event.presenter.viewmodel

import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {


    sealed class UiModel{
        object Loading: UiModel()
        object HideLoading: UiModel()
    }
}