package com.example.foreston

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import com.example.foreston.databinding.ActivityMisReportesBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class MisReportesActivity : AppCompatActivity(), OnFragmentGraphicListener,
    AdapterView.OnItemClickListener {

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

    private var listaDiametrosGrandis: ArrayList<Int> = ArrayList()
    private var listaDiametrosGlobulus: ArrayList<Int> = ArrayList()
    private var listaDiametrosCompletaGrandis: ArrayList<Int> = ArrayList()
    private var listaDiametrosCompletaGlobulus: ArrayList<Int> = ArrayList()
    private var listaIndustrias = doubleArrayOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Creo una instancia del primer fragment del gráfico, lo cargo directamente cuando cargo
        // también la tabla.

        iniciarListaGlobales()
        seleccionGrafico()
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

                    var industria :String

                    for (document in task) {

                        val diametroString = document.data.get("diametro_arboles").toString()
                        val cantArbolesString = document.data.get("cant_arboles").toString()
                        val especie = document.data.get("tipo").toString()
                        cargarDiametros(diametroString, cantArbolesString, especie)

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
                        putDoubleArray("INDUSTRIAS_VALORES",listaIndustrias)
                    }

                    fragmentTransaction.add(R.id.flGraficos, fragmentGraficoInicial)
                    fragmentTransaction.commit()

                }
        }
    }
    private fun seleccionGrafico() {
        val graficoString = resources.getStringArray(R.array.graficos)

        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_grafico, graficoString)
        binding.acGrFicosDisponibles.setAdapter(arrayAdapter)

        with(binding.acGrFicosDisponibles){
            onItemClickListener = this@MisReportesActivity
        }
    }
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val item = parent!!.getItemAtPosition(position).toString()

        when (item){
            "Torta - Rentabilidad por industria" -> {
                binding.acGrFicosDisponibles.setTextColor(getColor(R.color.totales_azul))
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
                    putDoubleArray("INDUSTRIAS_VALORES",listaIndustrias)

                }
                fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                fragmentTransaction.commit()
            }
            "Torta - Rentabilidad Porcentual" -> {
                binding.acGrFicosDisponibles.setTextColor(getColor(R.color.totales_azul))
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
                    putDoubleArray("INDUSTRIAS_VALORES",listaIndustrias)

                }
                fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                fragmentTransaction.commit()
            }
            "Barras - Distribución E. Grandis" -> {
                binding.acGrFicosDisponibles.setTextColor(getColor(R.color.teal_701))
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val nuevoGraficoFragment = GraficoDiametrosFragment()

                nuevoGraficoFragment.arguments = Bundle().apply {
                    putString("TIPO_GRAFICO", "BARRAS_VALOR")
                    putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
      //              putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosGrandis)
                    putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosCompletaGrandis)
                    putString("ESPECIE", "Eucalyptus Grandis")
                }

                fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                fragmentTransaction.commit()
            }
            "Barras - Distribución E. Globulus" -> {
                binding.acGrFicosDisponibles.setTextColor(getColor(R.color.teal_701))
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val nuevoGraficoFragment = GraficoDiametrosFragment()

                nuevoGraficoFragment.arguments = Bundle().apply {
                    putString("TIPO_GRAFICO", "BARRAS_VALOR")
                    putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                 //   putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosGlobulus)
                    putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosCompletaGlobulus)
                    putString("ESPECIE", "Eucalyptus Globulus")
                }

                fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                fragmentTransaction.commit()
            }
            "Barras - Distribución total" -> {
                binding.acGrFicosDisponibles.setTextColor(getColor(R.color.teal_701))
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val nuevoGraficoFragment = GraficoDiametrosFragment()

                val listaDiametrosTotal: ArrayList<Int> = ArrayList()

                for(num in 0..49) {
                    listaDiametrosTotal.add(num,listaDiametrosCompletaGrandis.get(num) + listaDiametrosCompletaGlobulus.get(num))
                }
                nuevoGraficoFragment.arguments = Bundle().apply {
                    putString("TIPO_GRAFICO", "BARRAS_VALOR")
                    putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                    putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosTotal)
                    putString("ESPECIE", "TODOS")
                }

                fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                fragmentTransaction.commit()
            }
            "Radar - Distribución E. Grandis" -> {
                binding.acGrFicosDisponibles.setTextColor(getColor(R.color.seleccion_magenta))
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val nuevoGraficoFragment = GraficoDiametrosPorIndustriaFragment()

                nuevoGraficoFragment.arguments = Bundle().apply {
                    putString("TIPO_GRAFICO", "RADAR_ARBOLES")
                    putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                    putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosGrandis)
                    putString("ESPECIE", "Eucalyptus Grandis")
                }

                fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                fragmentTransaction.commit()
            }
            "Radar - Distribución E. Globulus" -> {
                binding.acGrFicosDisponibles.setTextColor(getColor(R.color.seleccion_magenta))
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val nuevoGraficoFragment = GraficoDiametrosPorIndustriaFragment()

                nuevoGraficoFragment.arguments = Bundle().apply {
                    putString("TIPO_GRAFICO", "RADAR_ARBOLES")
                    putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                    putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosGlobulus)
                    putString("ESPECIE", "Eucalyptus Globulus")
                }

                fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                fragmentTransaction.commit()
            }
            "Radar - Distribución por especie" -> {
                binding.acGrFicosDisponibles.setTextColor(getColor(R.color.seleccion_magenta))
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val nuevoGraficoFragment = GraficoDiametrosPorIndustriaFragment()

                var sumador = 0
                var listaDiametrosAmbos: ArrayList<Int> = ArrayList()
                //pongo en la lista de Grandis los items de Globulus para enviar 1 sola lista

                for(num in 0..9) {
                    sumador = listaDiametrosGrandis.get(num)
                    listaDiametrosAmbos.add(num, sumador )
                }
                for(num in 0..9) {
                    sumador = listaDiametrosGlobulus.get(num)
                    listaDiametrosAmbos.add(num + 10, sumador)
                }
                nuevoGraficoFragment.arguments = Bundle().apply {
                    putString("TIPO_GRAFICO", "RADAR_ARBOLES")
                    putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                    putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosAmbos)
                    putString("ESPECIE", "AMBOS")
                }

                fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                fragmentTransaction.commit()
            }
            "Radar - Distribución Rentabilidad" -> {
                binding.acGrFicosDisponibles.setTextColor(getColor(R.color.seleccion_magenta))
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val nuevoGraficoFragment = GraficoRadarPorIndustriaFragment()

                var sumador = 0
                val listaDiametrosAmbos: ArrayList<Int> = ArrayList()
                //pongo en la lista de Grandis los items de Globulus para enviar 1 sola lista

                for(num in 0..9) {
                    sumador = listaDiametrosGrandis.get(num)
                    listaDiametrosAmbos.add(num, sumador )
                    sumador = 0
                }
                nuevoGraficoFragment.arguments = Bundle().apply {
                    putString("TIPO_GRAFICO", "RADAR_INDUSTRIAS")
                    putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                    putDoubleArray("INDUSTRIAS_VALORES",listaIndustrias)
                    putString("ESPECIE", "AMBOS")
                }
                fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                fragmentTransaction.commit()
            }
        }
    }
    private fun cargarDiametros(diametro: String, cantidad: String, especie: String) {

        val diametroInt = diametro.toInt()
        val cantidadInt = cantidad.toInt()
        var sumador = 0

        when (especie){
            "Eucalyptus Grandis" -> {
                listaDiametrosCompletaGrandis[diametroInt - 1] += cantidadInt

                when(diametroInt){
                    in 1..5 -> { sumador = listaDiametrosGrandis.get(0) + cantidadInt
                        listaDiametrosGrandis[0] = sumador
                    }

                    in 6..10 -> { sumador = listaDiametrosGrandis.get(1) + cantidadInt
                        listaDiametrosGrandis[1] = sumador
                    }

                    in 11..15 -> {sumador = listaDiametrosGrandis.get(2) + cantidadInt
                        listaDiametrosGrandis[2] = sumador
                    }

                    in 16..20 -> {sumador = listaDiametrosGrandis.get(3) + cantidadInt
                        listaDiametrosGrandis[3] = sumador
                    }

                    in 21..25 -> { sumador = listaDiametrosGrandis.get(4) + cantidadInt
                        listaDiametrosGrandis[4] = sumador
                    }

                    in 26..30 -> { sumador = listaDiametrosGrandis.get(5) + cantidadInt
                        listaDiametrosGrandis[5] = sumador
                    }

                    in 31..35 -> { sumador = listaDiametrosGrandis.get(6) + cantidadInt
                        listaDiametrosGrandis[6] = sumador
                    }

                    in 36..40 -> { sumador = listaDiametrosGrandis.get(7) + cantidadInt
                        listaDiametrosGrandis[7] = sumador
                    }

                    in 41..45 -> { sumador = listaDiametrosGrandis.get(8) + cantidadInt
                        listaDiametrosGrandis[8] = sumador
                    }

                    in 46..50 -> { sumador = listaDiametrosGrandis.get(9) + cantidadInt
                        listaDiametrosGrandis[9] = sumador

                    }
                }
            }
            "Eucalyptus Globulus" ->{
                listaDiametrosCompletaGlobulus[diametroInt - 1] += cantidadInt

                when(diametroInt){
                    in 1..5 -> { sumador = listaDiametrosGlobulus.get(0) + cantidadInt
                        listaDiametrosGlobulus[0] = sumador
                    }

                    in 6..10 -> { sumador = listaDiametrosGlobulus.get(1) + cantidadInt
                        listaDiametrosGlobulus[1] = sumador}

                    in 11..15 -> {sumador = listaDiametrosGlobulus.get(2) + cantidadInt
                        listaDiametrosGlobulus[2] = sumador}

                    in 16..20 -> {sumador = listaDiametrosGlobulus.get(3) + cantidadInt
                        listaDiametrosGlobulus[3] = sumador}

                    in 21..25 -> { sumador = listaDiametrosGlobulus.get(4) + cantidadInt
                        listaDiametrosGlobulus[4] = sumador}

                    in 26..30 -> { sumador = listaDiametrosGlobulus.get(5) + cantidadInt
                        listaDiametrosGlobulus[5] = sumador}

                    in 31..35 -> { sumador = listaDiametrosGlobulus.get(6) + cantidadInt
                        listaDiametrosGlobulus[6] = sumador}

                    in 36..40 -> { sumador = listaDiametrosGlobulus.get(7) + cantidadInt
                        listaDiametrosGlobulus[7] = sumador}

                    in 41..45 -> { sumador = listaDiametrosGlobulus.get(8) + cantidadInt
                        listaDiametrosGlobulus[8] = sumador}

                    in 46..50 -> { sumador = listaDiametrosGlobulus.get(9) + cantidadInt
                        listaDiametrosGlobulus[9] = sumador}
                }
            }
        }

    }

    private fun cargarRentabilidadPorIndustria(industria: String, rentabilidad: Double) {

        when (industria) {
            "Aserradero - En Monte en Pie" -> {
                aserraderoTotal += rentabilidad
                listaIndustrias[0] += rentabilidad
            }
            "Aserradero - En Playa de Monte" -> {
                aserraderoTotal += rentabilidad
                listaIndustrias[1] += rentabilidad
            }
            "Aserradero - En Playa de Monte - podado" -> {
                aserraderoTotal += rentabilidad
                listaIndustrias[1] += rentabilidad
            }
            "Aserradero - En Planta industrial" -> {
                aserraderoTotal += rentabilidad
                listaIndustrias[2] += rentabilidad
            }
            "Aserradero - En Planta industrial - podado" -> {
                aserraderoTotal += rentabilidad
                listaIndustrias[2] += rentabilidad
            }
            "Celulosa (C. B. Sta Fe)" -> {
                celulosaTotal += rentabilidad
                listaIndustrias[3] += rentabilidad
            }
            "Celulosa (Arauco Argentina SA)" -> {
                celulosaTotal += rentabilidad
                listaIndustrias[3] += rentabilidad
            }
            "Papelera (Papel Misionero SA)" -> {
                papelTotal += rentabilidad
                listaIndustrias[4] += rentabilidad
            }
            "Aserrin - En Planta industrial" -> {
                subproductosTotal += rentabilidad
                listaIndustrias[5] += rentabilidad
            }
            "Viruta - En Planta industrial" -> {
                subproductosTotal += rentabilidad
                listaIndustrias[5] += rentabilidad
            }
            "Chips - En Planta industrial" -> {
                subproductosTotal += rentabilidad
                listaIndustrias[5] += rentabilidad
            }
            "Costanero - En Planta industrial" -> {
                subproductosTotal += rentabilidad
                listaIndustrias[6] += rentabilidad
            }
            "Rodrigones - En Playa de Monte" -> {
                subproductosTotal += rentabilidad
                listaIndustrias[6] += rentabilidad
            }
            "Tutores - En Playa de Monte" -> {
                subproductosTotal += rentabilidad
                listaIndustrias[6] += rentabilidad
            }
            "Tijeras - En Playa de Monte" -> {
                subproductosTotal += rentabilidad
                listaIndustrias[6] += rentabilidad
            }
        }
    }

    private fun iniciarListaGlobales() {
        for(num in 0..9) {
            listaDiametrosGrandis.add(num,0)
            listaDiametrosGlobulus.add(num,0)
        }
        for(num in 0..49) {
            listaDiametrosCompletaGrandis.add(num,0)
            listaDiametrosCompletaGlobulus.add(num,0)
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

/*
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
                    binding.button5.setOnClickListener {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val nuevoGraficoFragment = GraficoDiametrosFragment()

                        nuevoGraficoFragment.arguments = Bundle().apply {
                            putString("TIPO_GRAFICO", "BARRAS_VALOR")
                            putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                            putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosGrandis)
                            putString("ESPECIE", "Eucalyptus Grandis")
                        }

                        fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                        fragmentTransaction.commit()
                    }
                    binding.button6.setOnClickListener {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val nuevoGraficoFragment = GraficoDiametrosFragment()

                        nuevoGraficoFragment.arguments = Bundle().apply {
                            putString("TIPO_GRAFICO", "BARRAS_VALOR")
                            putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                            putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosGlobulus)
                            putString("ESPECIE", "Eucalyptus Globulus")
                        }

                        fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                        fragmentTransaction.commit()
                    }
                    binding.button7.setOnClickListener {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val nuevoGraficoFragment = GraficoDiametrosFragment()

                        val listaDiametrosTotal: ArrayList<Int> = ArrayList()

                        for(num in 0..9) {
                            listaDiametrosTotal.add(num,listaDiametrosGrandis.get(num) + listaDiametrosGlobulus.get(num))
                        }

                        nuevoGraficoFragment.arguments = Bundle().apply {
                            putString("TIPO_GRAFICO", "BARRAS_VALOR")
                            putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                            putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosTotal)
                            putString("ESPECIE", "TODOS")
                        }

                        fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                        fragmentTransaction.commit()
                    }
                    binding.button8.setOnClickListener {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val nuevoGraficoFragment = GraficoDiametrosPorIndustriaFragment()

                        nuevoGraficoFragment.arguments = Bundle().apply {
                            putString("TIPO_GRAFICO", "RADAR_ARBOLES")
                            putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                            putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosGrandis)
                            putString("ESPECIE", "Eucalyptus Grandis")
                        }

                        fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                        fragmentTransaction.commit()
                    }
                    binding.button9.setOnClickListener {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val nuevoGraficoFragment = GraficoDiametrosPorIndustriaFragment()

                        nuevoGraficoFragment.arguments = Bundle().apply {
                            putString("TIPO_GRAFICO", "RADAR_ARBOLES")
                            putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                            putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosGlobulus)
                            putString("ESPECIE", "Eucalyptus Globulus")
                        }

                        fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                        fragmentTransaction.commit()
                    }
                    binding.button10.setOnClickListener {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val nuevoGraficoFragment = GraficoDiametrosPorIndustriaFragment()

                        var sumador = 0
                        var listaDiametrosAmbos: ArrayList<Int> = ArrayList()
                        //pongo en la lista de Grandis los items de Globulus para enviar 1 sola lista

                        for(num in 0..9) {
                            sumador = listaDiametrosGrandis.get(num)
                            listaDiametrosAmbos.add(num, sumador )
                            sumador = 0
                        }
                        for(num in 0..9) {
                            sumador = listaDiametrosGlobulus.get(num)
                            listaDiametrosAmbos.add(num + 10, sumador)
                            sumador = 0
                        }
                        nuevoGraficoFragment.arguments = Bundle().apply {
                            putString("TIPO_GRAFICO", "RADAR_ARBOLES")
                            putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                            putIntegerArrayList("LISTA_DIAMETROS", listaDiametrosAmbos)
                            putString("ESPECIE", "AMBOS")
                        }

                        fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                        fragmentTransaction.commit()
                    }
                    binding.button11.setOnClickListener {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val nuevoGraficoFragment = GraficoRadarPorIndustriaFragment()

                        var sumador = 0
                        val listaDiametrosAmbos: ArrayList<Int> = ArrayList()
                        //pongo en la lista de Grandis los items de Globulus para enviar 1 sola lista

                        for(num in 0..9) {
                            sumador = listaDiametrosGrandis.get(num)
                            listaDiametrosAmbos.add(num, sumador )
                            sumador = 0
                        }
                        nuevoGraficoFragment.arguments = Bundle().apply {
                            putString("TIPO_GRAFICO", "RADAR_INDUSTRIAS")
                            putInt("ARBOLES_TOTALES", arbolesTotalesGlobal )
                            putDoubleArray("INDUSTRIAS_VALORES",listaIndustrias)
                            putString("ESPECIE", "AMBOS")


                        }

                        fragmentTransaction.replace(R.id.flGraficos, nuevoGraficoFragment)
                        fragmentTransaction.commit()
                    }

 */