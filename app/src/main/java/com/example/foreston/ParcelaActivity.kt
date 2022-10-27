package com.example.foreston

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foreston.databinding.ActivityParcelasBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat


class ParcelaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParcelasBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParcelasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val nombreParcela = bundle!!.get("nombre_parcela") as String
        traerYcargarDatos(nombreParcela)

        binding.btnVolver.setOnClickListener {
            onBackPressed()
        }

/*
        binding.guardarParcela.setOnClickListener {
        if(maildeUsuario!=null && binding.NombreParcela.text.toString()!="" && binding.direParcela.text.toString()!=""
            && binding.cantArboles.text.toString()!=""
            && binding.diametro.text.toString()!=""
            && binding.altura.text.toString()!=""
            && binding.tipodearbol.text.toString()!=""
            && binding.edad.text.toString()!=""
            && binding.etIndustria.text.toString()!=""
        ){
            db.collection("users").document(maildeUsuario!!).collection("parcelas").document(binding.NombreParcela.text.toString()).set(hashMapOf(
            /*    "nombre_parcela" to binding.NombreParcela.text.toString(),
                "altura_prom" to binding.altura.text.toString(),
                "cant_arboles" to binding.cantArboles.text.toString(),
                "diametro_arboles" to binding.diametro.text.toString(),
                "tipo" to binding.tipodearbol.text.toString(),
                "edad" to binding.edad.text.toString(),
                "direccion" to binding.direParcela.text.toString(),*/
                "tipo_industria" to binding.etIndustria.text.toString()
                ))


            Toast.makeText(this, "Datos Actualizados", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, InformacionActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "completar todo los datos", Toast.LENGTH_SHORT).show()
        }
        }
*/
    }

    private fun traerYcargarDatos(nombreParcela : String) {

        val emailUser = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
            .getString(getString(R.string.Email), null)


        db.collection("users").document(emailUser!!).collection("parcelas").document(nombreParcela)
            .get().addOnSuccessListener {

                // Datos totales de la parcela

                val direccion = it.get("direccion")
                val hectareas = it.get("hectareas")
                val cantidadArboles = it.get("cant_arboles")
                val volumen = it.get("volumen_total")
                val pesoKilos = it.get("peso_total")
                var pesoToneladas = (pesoKilos.toString().toDouble())/1000
                val co2 = it.get("carbono_total")


                binding.etNombreParcela.text = "\"$nombreParcela\""
                binding.etUbicacion.text = direccion.toString()
                binding.etHectareas.text = hectareas.toString()
                binding.etCantidadArboles.text = cantidadArboles.toString()
                binding.etVolumen.text = "$volumen m3"
                binding.etPeso.text = "$pesoToneladas toneladas"
                binding.etCarbono.text = "$co2 toneladas"


                // Datos de las dimensiones de los arboles

                val diametro = it.get("diametro_arboles")
                val especie = it.get("tipo")
                val edadMeses = it.get("edad")
                val altura = it.get("altura_prom")
                val circunferencia = it.get("circunferencia_arboles")
                val fecha = it.get("fecha_escaneo")

                val edadAniosLong = edadMeses.toString().toLong()
                val edadAniosFloat = edadAniosLong.toFloat() / 12
                val df = DecimalFormat("##.##")
                val edadAnios = df.format(edadAniosFloat)


                binding.etDiametro.text = "$diametro cm"
                binding.etEspecie.text = especie.toString()
                binding.etEdad.text = "$edadAnios años"
                binding.etAltura.text = "$altura metros"
                binding.etCircunferencia.text = "$circunferencia cm"
                binding.etFecha.text = fecha.toString()

                // Datos económicos

                val industria = it.get("tipo_industria")
                val rentabilidad = it.get("valoracion_total")

                binding.etIndustria.text = industria.toString()
                binding.etRentabilidad.text = "$ $rentabilidad"
            }
    }

}