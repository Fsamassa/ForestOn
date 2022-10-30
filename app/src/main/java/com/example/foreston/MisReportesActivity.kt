package com.example.foreston

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.example.foreston.databinding.ActivityMisReportesBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class MisReportesActivity : AppCompatActivity(), OnFragmentGraphicListener {

    lateinit var binding: ActivityMisReportesBinding
    private val db = FirebaseFirestore.getInstance()

    private var hectareasTotalesGlobal = 0
    private var arbolesTotalesGlobal = 0
    private var pesoTotalGlobal = 0.0
    private var volumenTotalGlobal = 0.0
    private var carbonoTotalGlobal = 0.0
    private var rentabilidadTotalGlobal = 0.0

    private var aserraderoTotal = 0.0
    private var celulosaTotal = 0.0
    private var papelTotal = 0.0
    private var subproductosTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Creo una instancia del primer fragment del gráfico, lo cargo directamente cuando cargo
        // también la tabla.

        cargarTabla()

    }



    private fun cargarTabla() {

        val prefs = this.getSharedPreferences(
            getString(R.string.archivo_preferencias),
            Context.MODE_PRIVATE
        )
        val email = prefs.getString("Email", null)

        if (email != null) {
            db.collection("users").document(email).collection("parcelas").get()
                .addOnSuccessListener { task ->

                    var hectareasTotales = 0
                    var arbolesTotales = 0
                    var pesoTotal = 0.0
                    var volumenTotal = 0.0
                    var carbonoTotal = 0.0
                    var rentabilidadTotal = 0.0

                    var aserradero = 0.0
                    var celulosa = 0.0
                    var aserrin = 0.0
                    var chips = 0.0
                    var papel = 0.0
                    var tutores = 0.0
                    var tijeras = 0.0
                    var viruta = 0.0
                    var costanero = 0.0
                    var rodrigones = 0.0


                    var industria :String

                    for (document in task) {
                        val tablaContenedora = TableRow(this)
                        tablaContenedora.setPadding(15,5,15,5)

                        val fila1  = TextView(this)
                        fila1.text = document.data.get("nombre_parcela").toString()
                        fila1.textSize = 15.0F
                        fila1.setTextColor(getColor(R.color.black))
                        fila1.typeface = Typeface.DEFAULT_BOLD
                        tablaContenedora.addView(fila1)

                        val fila2  = TextView(this)
                        val hectareas = document.data.get("hectareas").toString()
                        fila2.text = hectareas
                        fila2.textSize = 15.0F
                        fila2.gravity = Gravity.CENTER
                        tablaContenedora.addView(fila2)

                        hectareasTotales += hectareas.toInt()

                        val fila3  = TextView(this)
                        val arboles = document.data.get("cant_arboles").toString()
                        fila3.text = "$arboles U."
                        fila3.textSize = 15.0F
                        fila3.gravity = Gravity.CENTER
                        tablaContenedora.addView(fila3)

                        arbolesTotales += arboles.toInt()

                        val fila4 = TextView(this)
                        val pesoKilos = document.data.get("peso_total")
                        val pesoToneladas = (pesoKilos.toString().toDouble())/1000

                        fila4.text = "${formatearDecimales(pesoToneladas)} ton"
                        fila4.textSize = 15.0F
                        fila4.gravity = Gravity.CENTER
                        tablaContenedora.addView(fila4)

                        pesoTotal += pesoToneladas

                        val fila5  = TextView(this)
                        val volumen = document.data.get("volumen_total").toString()
                        fila5.text = "${formatearDecimales(volumen.toDouble())} m3"
                        fila5.textSize = 15.0F
                        fila5.gravity = Gravity.CENTER
                        tablaContenedora.addView(fila5)

                        volumenTotal += volumen.toDouble()

                        val fila6  = TextView(this)
                        val carbono = document.data.get("carbono_total").toString()
                        fila6.text = "${formatearDecimales(carbono.toDouble())} ton"
                        fila6.textSize = 15.0F
                        fila6.gravity = Gravity.CENTER
                        tablaContenedora.addView(fila6)

                        carbonoTotal += carbono.toDouble()

                        val fila7  = TextView(this)
                        val rentabilidad = document.data.get("valoracion_total").toString()

                        fila7.text = "\$ ${formatearNumerosGrandes(rentabilidad.toDouble())}"
                        fila7.textSize = 15.0F
                        fila7.gravity = Gravity.CENTER
                        tablaContenedora.addView(fila7)

                        rentabilidadTotal += rentabilidad.toDouble()

                        val fila8  = TextView(this)
                        fila8.text = document.data.get("tipo_industria").toString()
                        fila8.textSize = 15.0F
                        fila8.gravity = Gravity.CENTER
                        tablaContenedora.addView(fila8)

                        binding.tlParcelas.addView(tablaContenedora)

                        industria = fila8.text.toString()

                        cargarRentabilidadPorIndustria(industria, rentabilidad.toDouble())

                    }

                    val tablaContenedora = TableRow(this)
                    tablaContenedora.setPadding(15,5,15,5)

                    val fila1  = TextView(this)
                    fila1.text = "TOTALES"
                    fila1.textSize = 20.0F
                    fila1.typeface = Typeface.DEFAULT_BOLD
                    fila1.setTextColor(getColor(R.color.barra_menu))
                    tablaContenedora.addView(fila1)

                    val fila2 = TextView(this)
                    fila2.text = "$hectareasTotales"
                    fila2.textSize = 15.0F
                    fila2.gravity = Gravity.CENTER
                    fila2.typeface = Typeface.DEFAULT_BOLD
                    fila2.setTextColor(getColor(R.color.totales_azul))
                    tablaContenedora.addView(fila2)

                    val fila3 = TextView(this)
                    fila3.text = "$arbolesTotales U."
                    fila3.textSize = 15.0F
                    fila3.gravity = Gravity.CENTER
                    fila3.typeface = Typeface.DEFAULT_BOLD
                    fila3.setTextColor(getColor(R.color.totales_azul))
                    tablaContenedora.addView(fila3)

                    val fila4 = TextView(this)

                    fila4.text = "${formatearDecimales(pesoTotal)} ton"
                    fila4.textSize = 15.0F
                    fila4.gravity = Gravity.CENTER
                    fila4.typeface = Typeface.DEFAULT_BOLD
                    fila4.setTextColor(getColor(R.color.totales_azul))
                    tablaContenedora.addView(fila4)

                    val fila5  = TextView(this)
                    fila5.text = "${formatearDecimales(volumenTotal)} m3"
                    fila5.textSize = 15.0F
                    fila5.gravity = Gravity.CENTER
                    fila5.typeface = Typeface.DEFAULT_BOLD
                    fila5.setTextColor(getColor(R.color.totales_azul))
                    tablaContenedora.addView(fila5)

                    val fila6  = TextView(this)
                    fila6.text = "${formatearDecimales(carbonoTotal)} ton"
                    fila6.textSize = 15.0F
                    fila6.gravity = Gravity.CENTER
                    fila6.typeface = Typeface.DEFAULT_BOLD
                    fila6.setTextColor(getColor(R.color.totales_azul))
                    tablaContenedora.addView(fila6)

                    val fila7  = TextView(this)
                    fila7.text = "\$ ${formatearNumerosGrandes(rentabilidadTotal)}"
                    fila7.textSize = 15.0F
                    fila7.gravity = Gravity.CENTER
                    fila7.typeface = Typeface.DEFAULT_BOLD
                    fila7.setTextColor(getColor(R.color.totales_azul))
                    tablaContenedora.addView(fila7)

                    hectareasTotalesGlobal = hectareasTotales
                    arbolesTotalesGlobal = arbolesTotales
                    pesoTotalGlobal = pesoTotal
                    volumenTotalGlobal = volumenTotal
                    carbonoTotalGlobal = carbonoTotal
                    rentabilidadTotalGlobal = rentabilidadTotal

                    binding.tlParcelas.addView(tablaContenedora)

                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    val fragmentGraficoInicial = GraficoInicialFragment()

                    fragmentGraficoInicial.arguments = Bundle().apply {
                        putString("TIPO_GRAFICO", "INDUSTRIAS_VALOR")
                        putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                        putInt("HECTARES_TOTALES", hectareasTotalesGlobal )
                        putDouble("ASERRADERO_TOTALES", aserraderoTotal )
                        putDouble("CELULOSA_TOTALES", celulosaTotal )
                        putDouble("PAPEL_TOTALES", papelTotal )
                        putDouble("SUBPRODUCTOS_TOTALES", subproductosTotal)
                    }

                    fragmentTransaction.add(R.id.flGraficos, fragmentGraficoInicial)
                    fragmentTransaction.commit()

                    binding.button3.setOnClickListener {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val nuevoGraficoFragment = GraficoInicialFragment()

                        nuevoGraficoFragment.arguments = Bundle().apply {
                            putString("TIPO_GRAFICO", "INDUSTRIAS_PORCENTAJE")
                            putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                            putInt("HECTARES_TOTALES", hectareasTotalesGlobal )
                            putDouble("ASERRADERO_TOTALES", aserraderoTotal )
                            putDouble("CELULOSA_TOTALES", celulosaTotal )
                            putDouble("PAPEL_TOTALES", papelTotal )
                            putDouble("SUBPRODUCTOS_TOTALES", subproductosTotal)

                        }
                        fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                        fragmentTransaction.commit()
                    }
                    binding.button4.setOnClickListener {
                          val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val nuevoGraficoFragment = GraficoInicialFragment()

                        nuevoGraficoFragment.arguments = Bundle().apply {
                            putString("TIPO_GRAFICO", "INDUSTRIAS_VALOR")
                            putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                            putInt("HECTARES_TOTALES", hectareasTotalesGlobal )
                            putDouble("ASERRADERO_TOTALES", aserraderoTotal )
                            putDouble("CELULOSA_TOTALES", celulosaTotal )
                            putDouble("PAPEL_TOTALES", papelTotal )
                            putDouble("SUBPRODUCTOS_TOTALES", subproductosTotal)

                        }
                        fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                        fragmentTransaction.commit()
                    }
                }
        }
    }

    private fun cargarRentabilidadPorIndustria(industria: String, rentabilidad: Double) {

        when(industria){
            "Aserradero - En Monte en Pie" -> aserraderoTotal += rentabilidad
            "Aserradero - En Playa de Monte" -> aserraderoTotal += rentabilidad
            "Aserradero - En Playa de Monte - podado" -> aserraderoTotal += rentabilidad
            "Aserradero - En Planta industrial" -> aserraderoTotal += rentabilidad
            "Aserradero - En Planta industrial - podado" -> aserraderoTotal += rentabilidad
            "Celulosa (C. B. Sta Fe)" -> celulosaTotal += rentabilidad
            "Celulosa (Arauco Argentina SA)" -> celulosaTotal += rentabilidad
            "Papel (Papel Misionero SA)" -> papelTotal += rentabilidad
            "Aserrin - En Planta industrial" -> subproductosTotal += rentabilidad
            "Viruta - En Planta industrial" -> subproductosTotal += rentabilidad
            "Costanero - En Planta industrial" -> subproductosTotal += rentabilidad
            "Rodrigones - En Playa de Monte" -> subproductosTotal += rentabilidad
            "Tutores - En Playa de Monte" -> subproductosTotal += rentabilidad
            "Tijeras - En Playa de Monte" -> subproductosTotal += rentabilidad
        }



    }

    private fun formatearNumerosGrandes(valor : Double): String? {
        val df = DecimalFormat("#,##0.00")
        return df.format(valor)
    }
    private fun formatearDecimales(valor : Double): String? {
        val df = DecimalFormat("#0.00")
        return df.format(valor)
    }

    override fun onClickFragmentButton() {
        val mensajeToast = Toast.makeText(
            this,
            "Toqué el botón del fragment gráfico", Toast.LENGTH_LONG)
        mensajeToast.setGravity(Gravity.CENTER,0 ,0)
        mensajeToast.show()
    }
}