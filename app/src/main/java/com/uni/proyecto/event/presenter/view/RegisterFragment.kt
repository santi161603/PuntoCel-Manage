package com.uni.proyecto.event.presenter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.uni.proyecto.event.MainActivity
import com.uni.proyecto.event.data.datasource.AuthenticationDataSourceImpl
import com.uni.proyecto.event.data.repository.AuthenticationRepositoryImpl
import com.uni.proyecto.event.databinding.FragmentRegisterBinding
import com.uni.proyecto.event.domain.datasource.AuthenticationDataSource
import com.uni.proyecto.event.domain.repository.AuthenticationRepository
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.auth
import com.uni.proyecto.event.domain.usecase.RegisterUserUseCase
import com.uni.proyecto.event.presenter.viewmodel.HomeViewModel
import com.uni.proyecto.event.presenter.viewmodel.RegisterViewModel
import com.uni.proyecto.event.presenter.viewmodel.RegisterViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val dataSource: AuthenticationDataSource = AuthenticationDataSourceImpl(auth)
    private val repository: AuthenticationRepository = AuthenticationRepositoryImpl(dataSource)
    private val registerUseCase = RegisterUserUseCase(repository)
    private val registerViewModelFactory = RegisterViewModelFactory(registerUseCase)
    private val registerViewModel: RegisterViewModel by viewModels{
        registerViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view: View = binding.root

        binding.btnRegistrar.setOnClickListener {
            registerViewModel.register(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }

        observerConfig()
        return view
    }

    private fun observerConfig(){
        registerViewModel.uiModel.observe(viewLifecycleOwner){
            when(it){
                RegisterViewModel.UiModel.Loading -> {
                    (requireActivity() as MainActivity).showLoadingAlert()
                    binding.btnRegistrar.isEnabled = false
                }
                RegisterViewModel.UiModel.HideLoading -> {
                    (requireActivity() as MainActivity).dismissLoadingAlert()
                    binding.btnRegistrar.isEnabled = true
                }

                is RegisterViewModel.UiModel.RegisterError -> {
                    (requireActivity() as MainActivity).ShowAlert(it.message)
                }

                RegisterViewModel.UiModel.RegisterUserFirestore -> {
                    (requireActivity() as MainActivity).ShowAlert("Usuario registrado con exito")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}