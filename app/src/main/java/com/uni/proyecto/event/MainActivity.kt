package com.uni.proyecto.event

import android.app.AlertDialog
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.uni.proyecto.event.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var dialog: AlertDialog? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.fragment_activity_main)
        navView.setupWithNavController(navController)

        drawerLayout = binding.container

        // Configurar el NavigationView
        val navViewTwo: NavigationView = findViewById(R.id.nav_view_dra)
        navViewTwo.setupWithNavController(navController)
        navViewTwo.setNavigationItemSelectedListener { menuItem ->
            // Manejar clic en elementos del menú
            when (menuItem.itemId) {
                R.id.nav_item_1 -> {
                    // Acciones para el Item 1
                }
                R.id.nav_item_2 -> {
                    // Acciones para el Item 2
                }
                R.id.nav_item_3 -> {
                    // Acciones para el Item 3
                }
            }
            // Cerrar el drawer al seleccionar un item
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val appBarConfiguration = AppBarConfiguration(
           setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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