package com.example.foreston

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import com.example.foreston.databinding.ActivityInfoScaneadaBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.DecimalFormat

class InfoScaneadaActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInfoScaneadaBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoScaneadaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val diametro = bundle!!.get("diametro") as String
        val imagenUri : Uri = bundle!!.get("imagen") as Uri

        binding.ivFotoScaneada.setImageURI(imagenUri)
        binding.etDiametro.text = "Diametro : " + diametro + " cm"

        traerDatosBaseDeDatos(diametro)
    }

    private fun traerDatosBaseDeDatos(diametro : String) {

        val idColeccionPorCM = "diametro_"+diametro+"cm"

        db.collection("tipos_arboles").document("eucalipto")
            .collection("eucalyptus grandis").document(idColeccionPorCM).get()
            .addOnSuccessListener {
            val altura = it.get("altura")
            val carbono = it.get("carbono")
            val circunferencia = it.get("circunferencia")
            val oxigeno = it.get("oxigeno")
            val peso = it.get("peso")
            val volumen = it.get("volumen")
            val edadMeses = it.get("edad")
                val edadAniosLong = edadMeses as Long

                val edadAniosFloat = edadAniosLong.toFloat() / 12
                val df = DecimalFormat("##.##")
                val edadAnios = df.format(edadAniosFloat)

                binding.etAltura.text         = "Altura   : " + altura + " mts"
                binding.etPeso.text           = "Peso     : " + peso + " kg"
                binding.etVolumen.text        = "Volumen  : " + volumen + " m3"
                binding.etCircunferencia.text = "Circunf. : " + circunferencia + " cm"
                binding.etOxigeno.text        = "Oxigeno  : " + oxigeno + " tn"
                binding.etCarbono.text        = "Carbono  : " + carbono + " tn"
                binding.etEdad.text           = "Edad     : " + edadMeses + " Meses ("+edadAnios+" años)"

        }
    }


}

