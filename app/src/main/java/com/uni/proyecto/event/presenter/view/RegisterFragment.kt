package com.uni.proyecto.event.presenter.view

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.uni.proyecto.event.MainActivity
import com.uni.proyecto.event.data.datasource.AuthenticationDataSourceImpl
import com.uni.proyecto.event.data.datasource.FireStoreDataSourceImp
import com.uni.proyecto.event.data.repository.AuthenticationRepositoryImpl
import com.uni.proyecto.event.data.repository.FireStoreRepositoryImp
import com.uni.proyecto.event.databinding.FragmentRegisterBinding
import com.uni.proyecto.event.domain.datasource.AuthenticationDataSource
import com.uni.proyecto.event.domain.datasource.FireStoreDataSource
import com.uni.proyecto.event.domain.repository.AuthenticationRepository
import com.uni.proyecto.event.domain.repository.FireStoreRepository
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.auth
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.firestore
import com.uni.proyecto.event.domain.usecase.RegisterFirestoreUserUseCase
import com.uni.proyecto.event.domain.usecase.RegisterUserUseCase
import com.uni.proyecto.event.presenter.viewmodel.RegisterViewModel
import com.uni.proyecto.event.presenter.viewmodel.RegisterViewModelFactory
import java.util.Calendar

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val dataSource: AuthenticationDataSource = AuthenticationDataSourceImpl(auth)
    private val dataSourceFS: FireStoreDataSource = FireStoreDataSourceImp(firestore)
    private val repositoryAuth: AuthenticationRepository = AuthenticationRepositoryImpl(dataSource)
    private val repositoryFS: FireStoreRepository = FireStoreRepositoryImp(dataSourceFS)
    private val registerUseCase = RegisterUserUseCase(repositoryAuth)
    private  val registerFirestoreUserUseCase = RegisterFirestoreUserUseCase(repositoryFS)
    private val registerViewModelFactory = RegisterViewModelFactory(registerUseCase, registerFirestoreUserUseCase)
    private val registerViewModel: RegisterViewModel by viewModels{
        registerViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view: View = binding.root

        binding. etFechaNacimiento.setOnClickListener {
            mostrarDatePicker(binding.etFechaNacimiento)
        }

        binding.btnRegistrar.setOnClickListener {
            registerViewModel.register(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString(),
                binding.repetirPasswordEditText.text.toString(),binding.nombreEditText.text.toString(), binding.apellidosEditText.text.toString(), binding.cedulaEditText.text.toString(),
                binding.celularEditText.text.toString(), binding.etFechaNacimiento.text.toString())
        }

        observerConfig()
        return view
    }

    private fun mostrarDatePicker(etFechaNacimiento: EditText) {
        // Fecha actual
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Configura los límites de edad
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                // Muestra la fecha seleccionada en el campo
                etFechaNacimiento.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
            },
            currentYear - 18, // Establece el año predeterminado como hace 18 años
            currentMonth,
            currentDay
        )

        // Configura la fecha mínima (100 años atrás)
        calendar.set(currentYear - 100, Calendar.JANUARY, 1)
        datePicker.datePicker.minDate = calendar.timeInMillis

        // Configura la fecha máxima (18 años atrás)
        calendar.set(currentYear - 18, currentMonth, currentDay)
        datePicker.datePicker.maxDate = calendar.timeInMillis

        // Muestra el DatePicker
        datePicker.show()
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
                    (requireActivity() as MainActivity).showAlert(it.message)
                }

                RegisterViewModel.UiModel.Successvalid -> {
                    (requireActivity() as MainActivity).showAlert("Su registro a sido exitoso"){
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}