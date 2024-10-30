package com.uni.proyecto.event.presenter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uni.proyecto.event.MainActivity
import com.uni.proyecto.event.R
import com.uni.proyecto.event.data.local.datastore.DataStoreManager
import com.uni.proyecto.event.databinding.FragmentLoginBinding
import com.uni.proyecto.event.databinding.FragmentProfileBinding
import com.uni.proyecto.event.databinding.FragmentRegisterBinding
import com.uni.proyecto.event.domain.singles.SingleInstancesFB.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view: View = binding.root

        val dataStoreManager = DataStoreManager(requireActivity())

        binding.sesion.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                dataStoreManager.deleteDataUser()
                // Cerrar sesión
                auth.signOut()
                // Navegar al fragmento de inicio de sesión
                findNavController().navigate(
                    R.id.loginFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.mobile_navigation, true).build()
                )
                // Habilitar el botón nuevamente
                binding.sesion.isEnabled = true
                (requireActivity() as MainActivity).closeDraweLayout()
            }

        }

        return view
    }

}