package com.uni.proyecto.event.presenter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.uni.proyecto.event.R
import com.uni.proyecto.event.databinding.FragmentDetailsProductBinding

class DetailsProductFragment : Fragment() {

    // Variable mutable para el binding, se inicializa en onCreateView
    private var _binding: FragmentDetailsProductBinding? = null

    // Propiedad inmutable para acceder al binding de forma segura
    private val binding get() = _binding!!

    // Delegado que obtiene los argumentos enviados desde la pantalla correspondiente
    private val args: DetailsProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsProductBinding.inflate(inflater, container, false)

    // Recupera el dato enviado
        val eventName = args.eventName

        // Usa eventName según lo necesites
        binding.tvNombre.text = eventName

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Evitar fugas de memoria
        _binding = null
    }
}