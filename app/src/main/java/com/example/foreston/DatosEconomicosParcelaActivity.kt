package com.example.foreston

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.foreston.databinding.ActivityDatosEconomicosParcelaBinding
import com.example.foreston.utils.GeneralUtils
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class DatosEconomicosParcelaActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var binding: ActivityDatosEconomicosParcelaBinding
    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_botton_anim) }
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private val db = FirebaseFirestore.getInstance()
    private var cantArbolesGlobal = 0
    private var pesoTotalGlobal = 0.0
    private var volumenTotalGlobal = 0.0
    private var industriaGlobal :String = ""
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosEconomicosParcelaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val idDiametro = bundle?.get("idDiametro") as String
        val idParcela = bundle.get("idParcela") as String
        val cantArboles = bundle.get("cantArboles") as Int
        val pesoTotal = bundle.get("pesoTotal") as Double
        val volumenTotal = bundle.get("volumenTotal") as Double

        cantArbolesGlobal = cantArboles
        pesoTotalGlobal = pesoTotal
        volumenTotalGlobal = volumenTotal

        binding.etCantArboles.text = cantArboles.toString()
        binding.etDiametroScaneado.text = idDiametro + " cm"
        binding.etPesoTotal.text = formatearNumerosGrandes(pesoTotal) + " ton"
        binding.etVolumenTotal.text = formatearNumerosGrandes(volumenTotal) + " m3"

        seleccionTipoIndustria()

        binding.btnConfiguracion.setOnClickListener{
            onAddButtonClicked()
        }
        binding.btnVolverHome.setOnClickListener{
            GeneralUtils.mostrarAlertaDecision(
                this,
                "¿ Deseas volver al Menú Principal ?",
                null, null,
                positiveAction = {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                negativeAction = null)
        }
        binding.btnGuardar.setOnClickListener{
            onAddButtonClicked()

            if (binding.etPrecioUnitario.text.isNullOrBlank() ||
                binding.etPrecioTotal.text.isNullOrBlank() ||
                binding.etRespuestaApto.text != "Si") {

                GeneralUtils.mostrarAlertaDecision(
                    this,
                    "La industria seleccionada no es la óptima para este diametro, ¿Deseas guardar los datos de todas formas?",
                    "Guardar", "Cancelar",
                    positiveAction = {
                        val maildeUsuario = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).getString(getString(R.string.Email), null)
                        val referenciaParcela = db.collection("users").document(maildeUsuario!!).collection("parcelas").document(idParcela)

                        referenciaParcela.update("tipo_industria", binding.acIndustriaDestino.text.toString(),
                            "valoracion_total", binding.etPrecioTotal.text.toString().removePrefix("$ ")).addOnFailureListener  {  Log.w(TAG, "Error actulizando industria en Firebase") }
               //         referenciaParcela.update( "valoracion_total", binding.etPrecioTotal.text.toString().removePrefix("$ ")).addOnFailureListener  {  Log.w(TAG, "Error actulizando industria en Firebase") }

                        val mensajeToast = Toast.makeText(this, "Datos actualizados exitosamente.", Toast.LENGTH_SHORT)
                        mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                        mensajeToast.show()
                    },
                    negativeAction = null)
            }else{
                val maildeUsuario = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).getString(getString(R.string.Email), null)
                val referenciaParcela = db.collection("users").document(maildeUsuario!!).collection("parcelas").document(idParcela)

                referenciaParcela.update("tipo_industria", binding.acIndustriaDestino.text.toString(),
                    "valoracion_total", binding.etPrecioTotal.text.toString().removePrefix("$ "))
                    .addOnFailureListener  {  Log.w(TAG, "Error actulizando industria en Firebase") }

      //          referenciaParcela.update( "valoracion_total", binding.etPrecioTotal.text.toString().removePrefix("$ ")).addOnFailureListener  {  Log.w(TAG, "Error actulizando industria en Firebase") }

                val mensajeToast = Toast.makeText(this, "Datos actualizados exitosamente.", Toast.LENGTH_SHORT)
                mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                mensajeToast.show()
            }
        }
    }

    private fun onAddButtonClicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }
    private fun setVisibility(clicked: Boolean) {
        if (!clicked){
            binding.btnGuardar.visibility = View.VISIBLE
            binding.btnVolverHome.visibility = View.VISIBLE
        }else{
            binding.btnGuardar.visibility = View.GONE
            binding.btnVolverHome.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked: Boolean) {
        if (!clicked){
            binding.btnGuardar.startAnimation(fromBottom)
            binding.btnVolverHome.startAnimation(fromBottom)
            binding.btnConfiguracion.startAnimation(rotateOpen)
        }else{
            binding.btnGuardar.startAnimation(toBottom)
            binding.btnVolverHome.startAnimation(toBottom)
            binding.btnConfiguracion.startAnimation(rotateClose)
        }
    }
    private fun seleccionTipoIndustria() {
        val industriaString = resources.getStringArray(R.array.industria_destino)

        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_industria, industriaString)
        binding.acIndustriaDestino.setAdapter(arrayAdapter)

        with(binding.acIndustriaDestino){
            onItemClickListener = this@DatosEconomicosParcelaActivity
        }

    }
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val industria = parent!!.getItemAtPosition(position).toString()
        industriaGlobal = industria
        val diametroAvalidar = binding.etDiametroScaneado.text.toString().removeSuffix(" cm").toInt()
        val docRef = db.collection("tipo_industria").document(industria)

        when (industria){
            "Aserradero - En Monte en Pie" -> {
                if(diametroAvalidar > 17){
                    docRef.get().addOnSuccessListener{
                        val precioUnitario = it.get("diametro_18cm")
                        binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                        binding.etRespuestaApto.text = "Si"
                        binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                        binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                    }
                }else{
                    if (diametroAvalidar > 11 && diametroAvalidar < 18){
                        docRef.get().addOnSuccessListener{
                            val precioUnitario = it.get("diametro_entre_12_y_17")
                            binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                            binding.etRespuestaApto.text = "Si"
                            binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                            binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                        }
                    }else{
                        if (diametroAvalidar < 11){
                            val diferenciaDiametro = 12 - diametroAvalidar
                            binding.etPrecioUnitario.text = "$ 0"
                            binding.etPrecioTotal.text = "$ 0"
                            binding.etRespuestaApto.text = "Diametro insuficiente - Restan " + diferenciaDiametro + " centímetros."
                            binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                        }
                    }
                }

            }
            "Aserradero - En Playa de Monte"-> {
                if(diametroAvalidar > 24){
                    docRef.get().addOnSuccessListener{
                        val precioUnitario = it.get("diametro_mayor_25")
                        binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                        binding.etRespuestaApto.text = "Si"
                        binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                        binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                    }
                }else{
                    if (diametroAvalidar > 13 && diametroAvalidar < 19){
                        docRef.get().addOnSuccessListener{
                            val precioUnitario = it.get("diametro_entre_14_y_18")
                            binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                            binding.etRespuestaApto.text = "Si"
                            binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                            binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                        }
                    }else{
                        if (diametroAvalidar > 18 && diametroAvalidar < 25){
                            docRef.get().addOnSuccessListener{
                                val precioUnitario = it.get("diametro_entre_19_y_25")
                                binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                                binding.etRespuestaApto.text = "Si"
                                binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                                binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                            }
                        }else{
                            if (diametroAvalidar > 6 && diametroAvalidar < 14){
                                docRef.get().addOnSuccessListener{
                                    val precioUnitario = it.get("diametro_entre_7_y_14")
                                    binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                                    binding.etRespuestaApto.text = "Si"
                                    binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                                    binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                                }
                            }else{
                                val diferenciaDiametro = 7 - diametroAvalidar
                                binding.etPrecioUnitario.text = "$ 0"
                                binding.etPrecioTotal.text = "$ 0"
                                binding.etRespuestaApto.text = "Diametro insuficiente - Restan " + diferenciaDiametro + " centímetros."
                                binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                            }
                        }

                    }
                }
            }
            "Aserradero - En Playa de Monte - podado"-> {
                if(diametroAvalidar > 24){
                    docRef.get().addOnSuccessListener{
                        val precioUnitario = it.get("diametro_mayor_25")
                        binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                        binding.etRespuestaApto.text = "Si"
                        binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                        binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                    }
                }else{
                    val diferenciaDiametro = 25 - diametroAvalidar
                    binding.etPrecioUnitario.text = "$ 0"
                    binding.etPrecioTotal.text = "$ 0"
                    binding.etRespuestaApto.text = "Diametro insuficiente - Restan " + diferenciaDiametro + " centímetros."
                    binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                }
            }
            "Aserradero - En Planta industrial"-> {
                if(diametroAvalidar > 24){
                    docRef.get().addOnSuccessListener{
                        val precioUnitario = it.get("diametro_mayor_25")
                        binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                        binding.etRespuestaApto.text = "Si"
                        binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                        binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                    }
                }else{
                    if (diametroAvalidar > 13 && diametroAvalidar < 19){
                        docRef.get().addOnSuccessListener{
                            val precioUnitario = it.get("diametro_entre_14_18")
                            binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                            binding.etRespuestaApto.text = "Si"
                            binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                            binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                        }
                    }else{
                        if (diametroAvalidar > 17 && diametroAvalidar < 25){
                            docRef.get().addOnSuccessListener{
                                val precioUnitario = it.get("diametro_entre_18_25")
                                binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                                binding.etRespuestaApto.text = "Si"
                                binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                                binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                            }
                        }else{
                            if (diametroAvalidar < 14){
                                val diferenciaDiametro = 15 - diametroAvalidar
                                binding.etPrecioUnitario.text = "$ 0"
                                binding.etPrecioTotal.text = "$ 0"
                                binding.etRespuestaApto.text = "Diametro insuficiente - Restan " + diferenciaDiametro + " centímetros."
                                binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                            }
                        }

                    }
                }
            }
            "Aserradero - En Planta industrial - podado"-> {
                if(diametroAvalidar > 24){
                    docRef.get().addOnSuccessListener{
                        val precioUnitario = it.get("diametro_mayor_25")
                        binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                        binding.etRespuestaApto.text = "Si"
                        binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                        binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                    }
                }else{
                    val diferenciaDiametro = 25 - diametroAvalidar
                    binding.etPrecioUnitario.text = "$ 0"
                    binding.etPrecioTotal.text = "$ 0"
                    binding.etRespuestaApto.text = "Diametro insuficiente - Restan " + diferenciaDiametro + " centímetros."
                    binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                }
            }
            "Aserrin - En Planta industrial"-> {
                docRef.get().addOnSuccessListener{
                    val precioUnitario = it.get("aserrin")
                    binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                    binding.etRespuestaApto.text = "Si"
                    binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                    binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                }
            }
            "Celulosa (C. B. Sta Fe)"-> {
                docRef.get().addOnSuccessListener{
                    val precioUnitario = it.get("chips_m3")
                    binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                    binding.etRespuestaApto.text = "Si"
                    binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                    binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(volumenTotalGlobal * precioUnitario.toString().toInt())
                }
            }
            "Celulosa (Arauco Argentina SA)"-> {
                docRef.get().addOnSuccessListener{
                    val precioUnitario = it.get("tronco_pulpable")
                    binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                    binding.etRespuestaApto.text = "Si"
                    binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                    binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                }
            }
            "Chips - En Planta industrial"-> {
                docRef.get().addOnSuccessListener{
                    val precioUnitario = it.get("chips")
                    binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                    binding.etRespuestaApto.text = "Si"
                    binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                    binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                }
            }
            "Costanero - En Planta industrial"-> {
                docRef.get().addOnSuccessListener{
                    val precioUnitario = it.get("costanero")
                    binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                    binding.etRespuestaApto.text = "Si"
                    binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                    binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                }
            }
            "Papel - Papel Misionero SA"-> {
                docRef.get().addOnSuccessListener{
                    val precioUnitario = it.get("chips")
                    binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                    binding.etRespuestaApto.text = "Si"
                    binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                    binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                }
            }
            "Rodrigones - En Playa de Monte"-> {
                if (diametroAvalidar > 4 && diametroAvalidar < 14){
                    docRef.get().addOnSuccessListener{
                        val precioUnitario = it.get("diametro_entre_5_13_unidad")
                        binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                        binding.etRespuestaApto.text = "Si"
                        binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                        binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(cantArbolesGlobal * precioUnitario.toString().toDouble())
                    }
                }else{
                    if (diametroAvalidar > 13 ){
                        val diferenciaDiametro = diametroAvalidar - 13
                        binding.etPrecioUnitario.text = "$ 0"
                        binding.etPrecioTotal.text = "$ 0"
                        binding.etRespuestaApto.text = "Diametro excedido por " + diferenciaDiametro + " centímetros."
                        binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                    }else{
                        val diferenciaDiametro = 14 - diametroAvalidar
                        binding.etPrecioUnitario.text = "$ 0"
                        binding.etPrecioTotal.text = "$ 0"
                        binding.etRespuestaApto.text = "Diametro insuficiente - Restan " + diferenciaDiametro + " centímetros."
                        binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                    }
                }
            }
            "Tutores - En Playa de Monte"-> {
                if (diametroAvalidar > 2 && diametroAvalidar < 6){
                    docRef.get().addOnSuccessListener{
                        val precioUnitario = it.get("diametro_entre_3_5")
                        binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                        binding.etRespuestaApto.text = "Si"
                        binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                        binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(cantArbolesGlobal * precioUnitario.toString().toDouble())
                    }
                }else{
                    if (diametroAvalidar > 5 ){
                        val diferenciaDiametro = diametroAvalidar - 5
                        binding.etPrecioUnitario.text = "$ 0"
                        binding.etPrecioTotal.text = "$ 0"
                        binding.etRespuestaApto.text = "Diametro excedido por " + diferenciaDiametro + " centímetros."
                        binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                    }else{
                        binding.etPrecioUnitario.text = "$ 0"
                        binding.etPrecioTotal.text = "$ 0"
                        binding.etRespuestaApto.text = "Diametro insuficiente - Resta 1 cm. "
                        binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                    }
                }
            }
            "Tijeras - En Playa de Monte"-> {
                if (diametroAvalidar == 9){
                    docRef.get().addOnSuccessListener{
                        val precioUnitario = it.get("diametro_9_unidad")
                        binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                        binding.etRespuestaApto.text = "Si"
                        binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                        binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(cantArbolesGlobal * precioUnitario.toString().toDouble())
                    }
                }else{
                    if (diametroAvalidar > 9 ){
                        val diferenciaDiametro = diametroAvalidar - 9
                        binding.etPrecioUnitario.text = "$ 0"
                        binding.etPrecioTotal.text = "$ 0"
                        binding.etRespuestaApto.text = "Diametro excedido por " + diferenciaDiametro + " centímetros."
                        binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                    }else{
                        val diferenciaDiametro = 9 - diametroAvalidar
                        binding.etPrecioUnitario.text = "$ 0"
                        binding.etPrecioTotal.text = "$ 0"
                        binding.etRespuestaApto.text = "Diametro insuficiente - Restan " + diferenciaDiametro + " centímetros."
                        binding.etRespuestaApto.setTextColor(getColor(R.color.seleccion_rojo))
                    }
                }
            }
            "Viruta - En Planta industrial"-> {
                docRef.get().addOnSuccessListener{
                    val precioUnitario = it.get("viruta")
                    binding.etPrecioUnitario.text = "$ " + precioUnitario.toString()
                    binding.etRespuestaApto.text = "Si"
                    binding.etRespuestaApto.setTextColor(getColor(R.color.purple_700))
                    binding.etPrecioTotal.text = "$ " + formatearNumerosGrandes(pesoTotalGlobal * precioUnitario.toString().toInt())
                }
            }
        }
    }

    private fun formatearNumerosGrandes(valor : Double): String? {
        val df = DecimalFormat("#,###.00")
        return df.format(valor)
    }


}

