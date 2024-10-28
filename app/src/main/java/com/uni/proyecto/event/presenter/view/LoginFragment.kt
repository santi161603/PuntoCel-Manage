package com.uni.proyecto.event.presenter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uni.proyecto.event.MainActivity
import com.uni.proyecto.event.R
import com.uni.proyecto.event.data.datasource.AuthenticationDataSourceImpl
import com.uni.proyecto.event.data.datasource.FireStoreDataSourceImp
import com.uni.proyecto.event.data.local.datastore.DataStoreManager
import com.uni.proyecto.event.data.repository.AuthenticationRepositoryImpl
import com.uni.proyecto.event.data.repository.FireStoreRepositoryImp
import com.uni.proyecto.event.databinding.FragmentHomeBinding
import com.uni.proyecto.event.databinding.FragmentLoginBinding
import com.uni.proyecto.event.domain.datasource.AuthenticationDataSource
import com.uni.proyecto.event.domain.datasource.FireStoreDataSource
import com.uni.proyecto.event.domain.exceptions.AuthException
import com.uni.proyecto.event.domain.repository.AuthenticationRepository
import com.uni.proyecto.event.domain.repository.FireStoreRepository
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.auth
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.firestore
import com.uni.proyecto.event.domain.usecase.GetEventosUseCase
import com.uni.proyecto.event.domain.usecase.LoginUseCase
import com.uni.proyecto.event.presenter.viewmodel.HomeViewModel
import com.uni.proyecto.event.presenter.viewmodel.HomeViewModelFactory
import com.uni.proyecto.event.presenter.viewmodel.LoginViewModel
import com.uni.proyecto.event.presenter.viewmodel.LoginViewModelFactory


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val dataSource: AuthenticationDataSource = AuthenticationDataSourceImpl(auth)
    private val repository: AuthenticationRepository = AuthenticationRepositoryImpl(dataSource)
    private val loguinUseCase = LoginUseCase(repository)
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var viewModelFactory: LoginViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view: View = binding.root
        binding.loginFragment.visibility = View.VISIBLE
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Aquí puedes obtener el contexto y crear el DataStoreManager
        val dataStoreManager = DataStoreManager(requireActivity())
        viewModelFactory = LoginViewModelFactory(loguinUseCase, dataStoreManager)
        // Inicializa el ViewModel con el factory
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            loginViewModel.login(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        observerConfig()
    }


    override fun onResume() {
        super.onResume()
        loginViewModel.validateUserData()
    }

    private fun observerConfig() {
        loginViewModel.uiModel.observe(viewLifecycleOwner) {
            when (it) {
                LoginViewModel.UiModel.HideLoading -> {
                    (requireActivity() as MainActivity).dismissLoadingAlert()
                    binding.loginButton.isEnabled = true
                }
                LoginViewModel.UiModel.Loading -> {
                    binding.loginButton.isEnabled = false
                    (requireActivity() as MainActivity).showLoadingAlert()
                }

                is LoginViewModel.UiModel.LoginError -> {
                    (requireActivity() as MainActivity).ShowAlert(it.message)
                }
                LoginViewModel.UiModel.LoginSuccess, LoginViewModel.UiModel.SessionSuccess -> {
                    if (findNavController().currentDestination?.id != R.id.homeFragment3) {
                        findNavController().navigate(
                            R.id.action_loginFragment_to_homeFragment3,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.mobile_navigation, true).build()
                        )
                    }
                }
                LoginViewModel.UiModel.SessionNull -> {
                    auth.signOut()
                    binding.loginFragment.visibility = View.VISIBLE
                    (requireActivity() as MainActivity).hideSplashScreen()
                }

                LoginViewModel.UiModel.HideSplash -> {
                    (requireActivity() as MainActivity).hideSplashScreen()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}