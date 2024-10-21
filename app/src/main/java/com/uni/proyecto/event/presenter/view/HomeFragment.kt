package com.uni.proyecto.event.presenter.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ListenerRegistration
import com.uni.proyecto.event.MainActivity
import com.uni.proyecto.event.data.datasource.FireStoreDataSourceImp
import com.uni.proyecto.event.data.repository.FireStoreRepositoryImp
import com.uni.proyecto.event.databinding.FragmentHomeBinding
import com.uni.proyecto.event.domain.datasource.FireStoreDataSource
import com.uni.proyecto.event.domain.repository.FireStoreRepository
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.firestore
import com.uni.proyecto.event.domain.usecase.GetEventosUseCase
import com.uni.proyecto.event.presenter.adapters.AdapterHome
import com.uni.proyecto.event.presenter.viewmodel.HomeViewModel
import com.uni.proyecto.event.presenter.viewmodel.HomeViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var listenerRegistration: ListenerRegistration? = null
    private val adapter by lazy { AdapterHome(this, mutableListOf(), requireContext()) }

    val dataSource: FireStoreDataSource = FireStoreDataSourceImp(firestore, listenerRegistration)
    val repository: FireStoreRepository = FireStoreRepositoryImp(dataSource)
    val getEventosUseCase: GetEventosUseCase = GetEventosUseCase(repository)
    val viewModelFactory = HomeViewModelFactory(getEventosUseCase)
    private val homeViewModel: HomeViewModel by viewModels {
    viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view: View = binding.root

        val layoutManager = GridLayoutManager(context,2)
        binding.rvHome.layoutManager = layoutManager
        binding.rvHome.adapter = adapter

        homeViewModel.getTiposEventos()

        observerConfig()
        return view
    }

    private fun observerConfig() {
        homeViewModel.uiModel.observe(viewLifecycleOwner){
            when(it){
                HomeViewModel.UiModel.Loading -> {
                    (requireActivity() as MainActivity).ShowLoadingAlert()
                }
                HomeViewModel.UiModel.EventosNoObtenidos -> {
                    (requireActivity() as MainActivity).ShowAlert("No se encontraron eventos")
                    binding.emply.visibility = View.VISIBLE
                }
                is HomeViewModel.UiModel.EventosObtenidos -> {
                    binding.emply.visibility = View.GONE
                    adapter.updateData(it.eventosList)
                }
                HomeViewModel.UiModel.HideLoading -> {
                    (requireActivity() as MainActivity).dismissLoadingAlert()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
        _binding = null
    }
}