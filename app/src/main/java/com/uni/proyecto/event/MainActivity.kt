package com.uni.proyecto.event

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.uni.proyecto.event.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var dialog: AlertDialog? = null
    private lateinit var drawerLayout: DrawerLayout
    private var isSessionChecked = false
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        splashScreen.setKeepOnScreenCondition { !isSessionChecked }

        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.fragment_activity_main)
        navView.setupWithNavController(navController)

        drawerLayout = binding.container

        // Configurar el NavigationView
        val navViewTwo: NavigationView = findViewById(R.id.nav_view_dra)
        navViewTwo.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment3, R.id.dashboardFragment2, R.id.notificationsFragment2, R.id.loginFragment
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configurar el ActionBar con el NavController
        configurateNavigationDrawer(navViewTwo)

        // Configurar el ActionBar con el NavController
        configurateActionBar(navView)

    }

    private fun configurateActionBar(navView: BottomNavigationView) {

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        // Manejar clicks en el botón de la barra de herramientas
        toggle.isDrawerIndicatorEnabled = true

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false) // Mostrar el botón "Atrás" // Acción cuando se desliza el menú
            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true) // Mostrar el botón "Atrás"
            }

            override fun onDrawerStateChanged(newState: Int) {
                // Acción cuando cambia el estado del menú
            }
        })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Ocultar el BottomNavigationView cuando se navega a ciertos destinos
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    navView.visibility = View.GONE
                    toggle.isDrawerIndicatorEnabled = false // Ocultar el botón para abrir el Drawer
                    supportActionBar?.setDisplayHomeAsUpEnabled(false) // Asegúrate de ocultar la flecha de retroceso
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) // Opcional: bloquear el Drawer
                }
                else -> {
                    navView.visibility = View.VISIBLE
                    toggle.isDrawerIndicatorEnabled = true // Mostrar el botón para abrir el Drawer
                    supportActionBar?.setDisplayHomeAsUpEnabled(true) // Asegúrate de ocultar la flecha de retroceso
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) // Asegúrate de desbloquear el Drawer
                }
            }
        }
    }

    private fun configurateNavigationDrawer(navViewTwo: NavigationView) {

        navViewTwo.setNavigationItemSelectedListener { menuItem ->
            // Manejar clic en elementos del menú
            when (menuItem.itemId) {
                R.id.Perfil -> {
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
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    fun ShowAlert(mss: String,onClickResult: (() -> Unit)? = null) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(mss)
        builder.setCancelable(false)
        builder.setPositiveButton(
            "Aceptar"
        ) { dialog, _ ->
            dialog.dismiss()
            onClickResult?.invoke()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun showLoadingAlert() {
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

    fun hideSplashScreen() {
        isSessionChecked = true
    }
}