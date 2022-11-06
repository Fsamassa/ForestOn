package com.example.foreston

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foreston.databinding.ActivityCertificacionesBinding
import com.example.foreston.utils.GeneralUtils
import com.google.firebase.firestore.FirebaseFirestore

class CertificacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertificacionesBinding
    private var mailuser: String? = null
    private val db2 = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarnombreParcela()

        db2.collection("users").document(mailuser!!).collection("parcelas").get().addOnSuccessListener {

            var cant_totalCarbono : Double = 0.0

            for (doc in it.documents){
                if(doc.get("carbono_total") == null) {
                    cant_totalCarbono = cant_totalCarbono + 0
                }else{
                    cant_totalCarbono = cant_totalCarbono + doc.get("carbono_total").toString().toDouble()
                }
            }
            binding.co2personales.text = GeneralUtils.formatearNumerosGrandes(cant_totalCarbono)
        }
    }

    fun cargarnombreParcela(){
        val bundle= intent.extras
        mailuser= bundle?.get("email") as String?
        println(mailuser)
    }
}