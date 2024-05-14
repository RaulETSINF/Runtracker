package com.mastermovilesua.runtrackerraul

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.mastermovilesua.runtrackerraul.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val isActivityRecognitionPermissionGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
            val isNotificationPermissionGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false

            if (isLocationPermissionGranted && isActivityRecognitionPermissionGranted && isNotificationPermissionGranted) {
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)

                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)

                val navController = findNavController(R.id.fragmentContainerView)
                binding.bottomNav.setupWithNavController(navController)
            } else {
                showPermissionSettingsDialog()
            }
            
        }

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        )

    }


    private fun showPermissionSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permisos requeridos")
            .setMessage("La aplicación requiere permisos de ubicación para funcionar correctamente. Por favor, habilite los permisos en la configuración de la aplicación.")
            .setPositiveButton("Configuración") { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

}