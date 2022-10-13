package com.example.foreston

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isGone
import com.example.foreston.databinding.ActivityInfoScaneadaBinding
import com.example.foreston.recyclerAsociados.RecyclerAsociadosActivity
import com.example.foreston.utils.GeneralUtils
import com.google.android.gms.common.api.Api
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class InfoScaneadaActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInfoScaneadaBinding
    private val db = FirebaseFirestore.getInstance()
    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_botton_anim) }
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private var clicked = false
    private var pesoDouble : Double = 0.0
    private var volumenDouble : Double = 0.0
    private var alturaDouble : Double = 0.0
    private var circunferenciaDouble : Double = 0.0
    private var edadMesesLong :  Long = 0
    private var oxigenoDouble :  Double = 0.0
    private var carbonoDouble :  Double = 0.0
    private var maildeUsuario : String? = null
    private var diametroACargar : Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoScaneadaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val diametro = bundle!!.get("diametro") as String
        diametroACargar = diametro.toInt()
        val imagenUri : Uri = bundle!!.get("imagen") as Uri

        binding.ivFotoScaneada.setImageURI(imagenUri)
        binding.etDiametro.text = "Diametro : " + diametro + " cm"

        val now = Date()
        val fecha = DateFormat.format("dd/MM/yyyy", now)
        binding.etFecha.text = fecha.toString()

        maildeUsuario = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).getString(getString(R.string.Email), null)

        traerDatosBaseDeDatos(diametro)

        binding.btnConfiguracion.setOnClickListener{
            onAddButtonClicked()
        }
        binding.btnCalcular.setOnClickListener{
            var mensajeToast = Toast.makeText(this, "CALCULANDO....", Toast.LENGTH_SHORT)

            if (binding.etCantArboles.text.isNotEmpty()){
                val cantArbolesString = binding.etCantArboles.text.toString()
                val cantArbolesInt = cantArbolesString.toInt()
                val pesoTotalFormat = cantArbolesInt * pesoDouble

                if (pesoTotalFormat > 5000){
                    binding.etPesoTotal.text = (pesoTotalFormat/1000).toString() + " ton"
                }else{
                    binding.etPesoTotal.text = pesoTotalFormat.toString() + " kg"
                }

                val df = DecimalFormat("##.##")
                val volTotalFormat = df.format((cantArbolesInt * volumenDouble))
                binding.etVolumenTotal.text = volTotalFormat.toString() + " m3"
                binding.etOxigenoTotal.text = (cantArbolesInt * oxigenoDouble).toString() + " ton"
                binding.etCo2Total.text = (cantArbolesInt * carbonoDouble).toString() + " ton"
                onAddButtonClicked()
            }else{
                mensajeToast = Toast.makeText(this, "¡ Ingresar cantidad de árboles !", Toast.LENGTH_LONG)
            }

            mensajeToast.setGravity(Gravity.CENTER,0 ,0)
            mensajeToast.show()
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

            if (binding.etCantArboles.text.isNullOrBlank() ||
                binding.etDireccion.text.isNullOrBlank() ||
                binding.etHectareaParcela.text.isNullOrBlank() ||
                binding.etNombreParcela.text.isNullOrBlank() ||
                binding.etIndustriaDestino.text.isNullOrBlank()) {

                val mensajeToast = Toast.makeText(this, "Falta ingresar información necesaria para guardar nueva parcela.", Toast.LENGTH_SHORT)
                mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                mensajeToast.show()
            }else{
                if (binding.etPesoTotal.text.isNullOrBlank()){
                    val mensajeToast = Toast.makeText(this, "Ingresar cantidad de árboles y generar calculos necesarios para guardar nueva parcela.", Toast.LENGTH_SHORT)
                    mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                    mensajeToast.show()
                }else{
                    guardarDatosDeParcela()
                }
            }
        }

    }

    private fun guardarDatosDeParcela() {
        val cantArbolesString = binding.etCantArboles.text.toString()
        val cantArbolesInt = cantArbolesString.toInt()

        db.collection("users").document(maildeUsuario!!).collection("parcelas").document(binding.etNombreParcela.text.toString()).set(hashMapOf(
            "nombre_parcela" to binding.etNombreParcela.text.toString(),
            "altura_prom" to alturaDouble.toString(),
            "cant_arboles" to binding.etCantArboles.text.toString(),
            "diametro_arboles" to diametroACargar.toString(),
            "tipo" to "Eucaliptus Grandis",
            "edad" to edadMesesLong.toString(),
            "direccion" to binding.etDireccion.text.toString(),
            "tipo_industria" to binding.etIndustriaDestino.text.toString(),
            "circunferencia_arboles" to circunferenciaDouble,
            "peso_total" to (cantArbolesInt * pesoDouble).toString(),
            "volumen_total" to (cantArbolesInt * volumenDouble).toString(),
            "oxigeno_total" to (cantArbolesInt * oxigenoDouble).toString(),
            "carbono_total" to (cantArbolesInt * carbonoDouble).toString(),
        ))
        GeneralUtils.mostrarAlertaDecision(
            this,
            "Nueva parcela cargada y guardada exitosamente ¿Deseas volver al Menú Principal?",
            null, null,
            positiveAction = {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            },
            negativeAction = null)
    }

    private fun onAddButtonClicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }
    private fun setVisibility(clicked: Boolean) {
        if (!clicked){
            binding.btnCalcular.visibility = View.VISIBLE
            binding.btnGuardar.visibility = View.VISIBLE
            binding.btnVolverHome.visibility = View.VISIBLE
        }else{
            binding.btnCalcular.visibility = View.GONE
            binding.btnGuardar.visibility = View.GONE
            binding.btnVolverHome.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked: Boolean) {
        if (!clicked){
            binding.btnCalcular.startAnimation(fromBottom)
            binding.btnGuardar.startAnimation(fromBottom)
            binding.btnVolverHome.startAnimation(fromBottom)
            binding.btnConfiguracion.startAnimation(rotateOpen)
        }else{
            binding.btnCalcular.startAnimation(toBottom)
            binding.btnGuardar.startAnimation(toBottom)
            binding.btnVolverHome.startAnimation(toBottom)
            binding.btnConfiguracion.startAnimation(rotateClose)
        }
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
                binding.etOxigeno.text        = "Oxigeno  : " + oxigeno + " ton"
                binding.etCarbono.text        = "Carbono  : " + carbono + " ton"
                binding.etEdad.text           = "Edad     : " + edadMeses + " Meses ("+edadAnios+" años)"

                alturaDouble = altura.toString().toDouble()
                circunferenciaDouble = circunferencia.toString().toDouble()
                edadMesesLong = edadAniosLong
                pesoDouble = peso.toString().toDouble()
                volumenDouble = volumen.toString().toDouble()
                oxigenoDouble = oxigeno.toString().toDouble()
                carbonoDouble = carbono.toString().toDouble()

        }
    }
}


