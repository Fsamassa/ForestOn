package com.example.foreston

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.foreston.databinding.ActivityInfoScaneadaBinding
import com.example.foreston.utils.GeneralUtils
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.util.*

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
    private var flagDatosGuardados = false
    private var pesototal : Double = 0.0
    private var volumentotal : Double = 0.0
    private var cantArbolesGlobal : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoScaneadaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val diametro = bundle!!.get("diametro") as String
        diametroACargar = diametro.toInt()

        binding.etDiametro.text = "Diametro : " + diametro + " cm"

        val especie = bundle.get("especie") as String

        if (especie.isNotBlank()){
            binding.etEspecie.text = "Especie: $especie"
            binding.tvMedidasScaneadas.text = "Medidas Ingresadas"

            if (especie == "Eucalyptus Grandis") {
                binding.ivFotoScaneada.setImageResource(R.drawable.eucaliptus_grandis_default)
            }else{
                binding.ivFotoScaneada.setImageResource(R.drawable.eucaliptus_globulus_default)
                binding.tvNombreComun.text = "Nombre: Eucalipto blanco"
            }
        }else{
            val imagenUri : Uri = bundle.get("imagen") as Uri
            binding.ivFotoScaneada.setImageURI(imagenUri)
        }

        val now = Date()
        val fecha = DateFormat.format("dd/MM/yyyy", now)
        binding.etFecha.text = fecha.toString()

        maildeUsuario = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).getString(getString(R.string.Email), null)

        traerDatosBaseDeDatos(diametro)

        binding.btnConfiguracion.setOnClickListener{
            onAddButtonClicked()
        }

        binding.btnDatosEconomicos.setOnClickListener{
            onAddButtonClicked()

            if (binding.etCantArboles.text.isNullOrBlank() ||
                binding.etDireccion.text.isNullOrBlank() ||
                binding.etHectareaParcela.text.isNullOrBlank() ||
                binding.etNombreParcela.text.isNullOrBlank()) {

                val mensajeToast = Toast.makeText(this, "Falta ingresar información necesaria para calcular datos Económicos.", Toast.LENGTH_LONG)
                mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                mensajeToast.show()
            }else{
                if (binding.etPesoTotal.text.isNullOrBlank()){
                    val mensajeToast = Toast.makeText(this, "Ingresar cantidad de árboles y generar calculos necesarios para ver datos Económicos.", Toast.LENGTH_LONG)
                    mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                    mensajeToast.show()
                }else{
                    generarDatosEconomicos(diametro)
                }
            }
        }


        binding.btnCalcular.setOnClickListener{
            var mensajeToast = Toast.makeText(this, "Calculando...", Toast.LENGTH_SHORT)

            if (binding.etCantArboles.text.isNotEmpty()){
                val cantArbolesString = binding.etCantArboles.text.toString()
                val cantArbolesInt = cantArbolesString.toInt()
                val pesoTotalFormat = cantArbolesInt * pesoDouble

                val df = DecimalFormat("#,###.00")

                if (pesoTotalFormat > 5000){
                    binding.etPesoTotal.text = df.format((pesoTotalFormat/1000)) + " ton"
                }else{
                    binding.etPesoTotal.text = df.format(pesoTotalFormat) + " kg"
                }

                binding.etVolumenTotal.text = df.format((cantArbolesInt * volumenDouble)) + " m3"
                binding.etOxigenoTotal.text = df.format((cantArbolesInt * oxigenoDouble)) + " ton"
                binding.etCo2Total.text = df.format((cantArbolesInt * carbonoDouble)) + " ton"


                pesototal = pesoTotalFormat/1000
                volumentotal = cantArbolesInt * volumenDouble
                cantArbolesGlobal = cantArbolesInt

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
                binding.etNombreParcela.text.isNullOrBlank()) {

                val mensajeToast = Toast.makeText(this, "Falta ingresar información necesaria para guardar nueva parcela.", Toast.LENGTH_LONG)
                mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                mensajeToast.show()
            }else{
                if (binding.etPesoTotal.text.isNullOrBlank()){
                    val mensajeToast = Toast.makeText(this, "Ingresar cantidad de árboles y generar cálculos necesarios para guardar nueva parcela.", Toast.LENGTH_LONG)
                    mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                    mensajeToast.show()
                }else{
                    guardarDatosDeParcela()
                }
            }
        }

    }

    private fun generarDatosEconomicos(diametro: String) {
        val idDiametro = diametro
        if (flagDatosGuardados){
            GeneralUtils.mostrarAlertaDecision(
                this,
                "¿Iniciar generador de datos Económicos para esta Parcela? Recuerda que los datos que hayas modificado y no guardado se perderán.",
                "Generar Datos Económicos", "Revisar datos",
                positiveAction = {
                    val intent = Intent(this, DatosEconomicosParcelaActivity::class.java)
                    intent.putExtra("idDiametro", idDiametro)
                    intent.putExtra("idParcela", binding.etNombreParcela.text.toString())
                    intent.putExtra("cantArboles", cantArbolesGlobal)
                    intent.putExtra("pesoTotal", pesototal)
                    intent.putExtra("volumenTotal", volumentotal)
                    startActivity(intent)
                    finish()
                },
                negativeAction = null)
        }else{
            GeneralUtils.mostrarAlerta(
                this,
                "No has guardado los datos cargados! ForestOn necesita que guardes los datos para avanzar.",
                null, null)
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
            "tipo" to binding.etEspecie.text.removePrefix("Especie: "),
            "edad" to edadMesesLong.toString(),
            "direccion" to binding.etDireccion.text.toString(),
            "circunferencia_arboles" to circunferenciaDouble,
            "peso_total" to (cantArbolesInt * pesoDouble).toString(),
            "volumen_total" to (cantArbolesInt * volumenDouble).toString(),
            "oxigeno_total" to (cantArbolesInt * oxigenoDouble).toString(),
            "carbono_total" to (cantArbolesInt * carbonoDouble).toString(),
            "fecha_escaneo" to  binding.etFecha.text.toString(),
            "hectareas" to binding.etHectareaParcela.text.toString(),
            "tipo_industria" to "",
            "valoracion_total" to "0.0",

        ))
        flagDatosGuardados = true
        GeneralUtils.mostrarAlertaDecision(
            this,
            "Nueva parcela cargada y guardada exitosamente ¿Deseas generar datos Económicos?",
            null, null,
            positiveAction = {
                val intent = Intent(this, DatosEconomicosParcelaActivity::class.java)
                intent.putExtra("idDiametro", diametroACargar.toString())
                intent.putExtra("idParcela", binding.etNombreParcela.text.toString())
                intent.putExtra("cantArboles", cantArbolesGlobal)
                intent.putExtra("pesoTotal", pesototal)
                intent.putExtra("volumenTotal", volumentotal)
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
            binding.btnDatosEconomicos.visibility = View.VISIBLE
        }else{
            binding.btnCalcular.visibility = View.GONE
            binding.btnGuardar.visibility = View.GONE
            binding.btnVolverHome.visibility = View.GONE
            binding.btnDatosEconomicos.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked: Boolean) {
        if (!clicked){
            binding.btnCalcular.startAnimation(fromBottom)
            binding.btnGuardar.startAnimation(fromBottom)
            binding.btnVolverHome.startAnimation(fromBottom)
            binding.btnDatosEconomicos.startAnimation(fromBottom)
            binding.btnConfiguracion.startAnimation(rotateOpen)
        }else{
            binding.btnCalcular.startAnimation(toBottom)
            binding.btnGuardar.startAnimation(toBottom)
            binding.btnVolverHome.startAnimation(toBottom)
            binding.btnDatosEconomicos.startAnimation(toBottom)
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
                binding.etOxigeno.text        = "Oxígeno  : " + oxigeno + " ton"
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


