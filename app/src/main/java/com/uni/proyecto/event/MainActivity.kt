package com.uni.proyecto.event

import android.app.AlertDialog
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.uni.proyecto.event.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

       // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun ShowAlert(mss: String,onClickResult: (() -> Unit)? = null) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(mss)
        builder.setCancelable(false)
        builder.setPositiveButton(
            "Aceptar"
        ) { dialog, which ->
            dialog.dismiss()
            onClickResult?.invoke()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun ShowLoadingAlert() {
        // Crear el LayoutInflater para inflar el diseño personalizado
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)

        // Crear el AlertDialog con el diseño personalizado
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Hacer que el diálogo no sea cancelable

        dialog = builder.create()

        // Mostrar el diálogo
        dialog?.show()
    }
    fun dismissLoadingAlert() {
        dialog?.dismiss()
    }
}