package com.uni.proyecto.event.presenter.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.uni.proyecto.event.MainActivity
import com.uni.proyecto.event.R
import com.uni.proyecto.event.data.datasource.FireStoreDataSourceImp
import com.uni.proyecto.event.data.datasource.StorageDataSourceImp
import com.uni.proyecto.event.data.repository.FireStoreRepositoryImp
import com.uni.proyecto.event.data.repository.StorageRepositoryImp
import com.uni.proyecto.event.databinding.FragmentHomeBinding
import com.uni.proyecto.event.databinding.FragmentManageEventsBinding
import com.uni.proyecto.event.domain.datasource.FireStoreDataSource
import com.uni.proyecto.event.domain.datasource.StorageDataSource
import com.uni.proyecto.event.domain.repository.FireStoreRepository
import com.uni.proyecto.event.domain.repository.StorageRepository
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.firestore
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.storage
import com.uni.proyecto.event.domain.usecase.AddEventFirestoreUseCase
import com.uni.proyecto.event.domain.usecase.GetEventosUseCase
import com.uni.proyecto.event.domain.usecase.UpdateImageUseCase
import com.uni.proyecto.event.presenter.viewmodel.HomeViewModel
import com.uni.proyecto.event.presenter.viewmodel.HomeViewModelFactory
import com.uni.proyecto.event.presenter.viewmodel.ManageEventsModelFactory
import com.uni.proyecto.event.presenter.viewmodel.ManageEventsViewModel

class ManageEventsFragment : Fragment() {

    private var _binding: FragmentManageEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var getImage: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    private val dataSource: FireStoreDataSource = FireStoreDataSourceImp(firestore)
    private val dataSourceStorage: StorageDataSource = StorageDataSourceImp(storage)
    private val repository: FireStoreRepository = FireStoreRepositoryImp(dataSource)
    private val repositoryStorage: StorageRepository = StorageRepositoryImp(dataSourceStorage)
    private val updateImageUseCase: UpdateImageUseCase = UpdateImageUseCase(repositoryStorage)
    private val addEventFirestoreUseCase: AddEventFirestoreUseCase =
        AddEventFirestoreUseCase(repository)
    private val viewModelFactory =
        ManageEventsModelFactory(updateImageUseCase, addEventFirestoreUseCase)
    private val managerEventViewModel: ManageEventsViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageEventsBinding.inflate(inflater, container, false)
        val view: View = binding.root

        getImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    imageUri = result.data?.data

                    if (imageUri != null) {
                        binding.btnAgregarImagen.text = imageUri.toString()
                    }
                }
            }

        binding.btnAgregarImagen.setOnClickListener {
            // Lógica para seleccionar una imagen
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            getImage.launch(intent) // Usar el ActivityResultLauncher para lanzar el intent
        }

        binding.btnAgregarEvento.setOnClickListener {
            managerEventViewModel.validData(
                binding.edNombre.text.toString(),
                binding.edCostoMinimo.text.toString(),
                binding.edDescripcion.text.toString(),
                binding.edMiniDescripcion.text.toString(),
                imageUri,
                binding.edArrayStrings.text.toString()
            )
        }

        observerConfig()
        return view
    }

    private fun observerConfig() {
        managerEventViewModel.uiModel.observe(viewLifecycleOwner) {
            when (it) {
                is ManageEventsViewModel.UiModel.EventosObtenidos -> {

                }

                ManageEventsViewModel.UiModel.HideLoading -> {
                    (requireActivity() as MainActivity).dismissLoadingAlert()
                    binding.btnAgregarEvento.isEnabled = true
                }

                ManageEventsViewModel.UiModel.Loading -> {
                    (requireActivity() as MainActivity).showLoadingAlert()
                    binding.btnAgregarEvento.isEnabled = false
                }

                is ManageEventsViewModel.UiModel.Error -> {
                    (requireActivity() as MainActivity).showAlert(it.mss)
                }

                ManageEventsViewModel.UiModel.EventoAnadido -> {
                    (requireActivity() as MainActivity).showAlert("Evento agregado correctamente") {
                        findNavController().popBackStack()
                    }
                }
            }

        }

    }

}