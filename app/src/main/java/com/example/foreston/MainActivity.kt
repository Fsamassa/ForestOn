package com.example.foreston

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.foreston.databinding.MainActivityBinding
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {

    lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sleep de carga forzada para ver la pantalla de carga
        Thread.sleep(2000)

        screenSplash.setKeepOnScreenCondition { false }

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("mensaje", "Integración con Firebase completa")
        analytics.logEvent("Ingreso_a_MainActivity_Foreston", bundle)

        // Voy directo a la activity de autorización
        val intent = Intent(this, IngresoActivity::class.java)
        startActivity(intent)

        finish()
    }
}