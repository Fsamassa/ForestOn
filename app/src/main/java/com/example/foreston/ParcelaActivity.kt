package com.example.foreston

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foreston.databinding.ActivityParcelasBinding
import com.example.foreston.recyclerParcelas.RecyclerParcelasActivity
import com.example.foreston.utils.GeneralUtils
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

        val emailUser = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
            .getString(getString(R.string.Email), null)


        traerYcargarDatos(nombreParcela, emailUser!!)

        binding.btnVolver.setOnClickListener {
            val intent= Intent(this, RecyclerParcelasActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnBorrarParcela.setOnClickListener {
            GeneralUtils.mostrarAlertaDecision(
                this,
                "¿Confirmar la eliminación definitiva de la parcela \"$nombreParcela\" y volver al listado anterior?",
            "Eliminar",
            "Cancelar",
                positiveAction = {
                    db.collection("users").document(emailUser).collection("parcelas").document(nombreParcela)
                        .delete().addOnSuccessListener{
                         /*   GeneralUtils.mostrarAlerta(
                                this,
                                "Parcela eliminada satisfactoriamente")

                          */
                        }
                    val intent= Intent(this, RecyclerParcelasActivity::class.java)
                    startActivity(intent)
                    finish()
            },
                negativeAction = null)
        }
    }

    private fun traerYcargarDatos(nombreParcela : String, emailUser : String) {

        db.collection("users").document(emailUser).collection("parcelas").document(nombreParcela)
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
                binding.etVolumen.text = "${GeneralUtils.formatearDecimales(volumen.toString().toDouble())} m3"
                binding.etPeso.text = "${GeneralUtils.formatearDecimales(pesoToneladas)} toneladas"
                binding.etCarbono.text = "${GeneralUtils.formatearDecimales(co2.toString().toDouble())} toneladas"

                // Datos de las dimensiones de los arboles

                val diametro = it.get("diametro_arboles")
                val especie = it.get("tipo")
                val edadMeses = it.get("edad")
                val altura = it.get("altura_prom")
                val circunferencia = it.get("circunferencia_arboles")
                val fecha = it.get("fecha_escaneo")

                val anios = GeneralUtils.pasarEdadanios(edadMeses.toString())

                binding.etDiametro.text = "$diametro cm"
                binding.etEspecie.text = especie.toString()
                binding.etEdad.text = "$anios"
                binding.etAltura.text = "$altura metros"
                binding.etCircunferencia.text = "${circunferencia.toString().removeSuffix(".0")} cm"
                binding.etFecha.text = fecha.toString()

                // Datos económicos

                val industria = it.get("tipo_industria")
                val rentabilidad = it.get("valoracion_total")
                val rentabilidadFormateada = GeneralUtils.formatearNumerosGrandes(rentabilidad.toString().toDouble())

                binding.etIndustria.text = industria.toString()
                binding.etRentabilidad.text = "$ $rentabilidadFormateada"
            }
    }

}