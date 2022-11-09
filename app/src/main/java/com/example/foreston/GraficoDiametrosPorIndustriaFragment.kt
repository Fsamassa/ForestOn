package com.example.foreston

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foreston.databinding.FragmentGraficoDiametrosPorIndustriaBinding
import com.example.foreston.utils.GeneralUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

private const val ARG_PARAM0 = "TIPO_GRAFICO"
private const val ARG_PARAM1 = "ARBOLES_TOTALES"
private const val ARG_PARAM2 = "LISTA_DIAMETROS"
private const val ARG_PARAM3 = "ESPECIE"

class GraficoDiametrosPorIndustriaFragment : Fragment() {

    private var _binding: FragmentGraficoDiametrosPorIndustriaBinding? = null
    private val binding get() = _binding!!

    private var tipoGrafico: String? = ""
    private var arbolesTotales: Int? = 0
    private var unidadesPorDiametro : ArrayList<Int> = ArrayList()
    private var especie : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tipoGrafico = it.getString(ARG_PARAM0)
            arbolesTotales = it.getInt(ARG_PARAM1)
            unidadesPorDiametro = it.getIntegerArrayList(ARG_PARAM2) as ArrayList<Int>
            especie = it.getString(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraficoDiametrosPorIndustriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.takeIf { it.containsKey(ARG_PARAM0) }?.apply {

            var totalEspecie = 0f
            val entries: ArrayList<RadarEntry> = ArrayList()
            val radarData : RadarData = RadarData()
            for(num in 0..9) {
                totalEspecie += unidadesPorDiametro.get(num).toFloat()
                entries.add(RadarEntry( unidadesPorDiametro.get(num).toFloat()))
            }
            val totalEspecieFormateado = GeneralUtils.formatearNumerosGrandes(totalEspecie.toDouble())
            val arbolesTotalesFormateado = GeneralUtils.formatearNumerosGrandes(arbolesTotales!!.toDouble())

            when (tipoGrafico){

                "RADAR_ARBOLES" -> {
                    when (especie){
                        "Eucalyptus Grandis" -> {
                            binding.tvCantArboles.text = "Distribución de $totalEspecieFormateado ejemplares de $especie"
                            binding.grafRadar.description.text = "Distribución Eucalyptus Grandis"
                            binding.grafRadar.description.textSize = 14f
                        }
                        "Eucalyptus Globulus" -> {
                            binding.tvCantArboles.text = "Distribución de $totalEspecieFormateado ejemplares de $especie"
                            binding.grafRadar.description.text = "Distribución Eucalyptus Globulus"
                            binding.grafRadar.description.textSize = 14f
                        }
                        "AMBOS" -> {
                            binding.tvCantArboles.text = "Distribución general de $arbolesTotalesFormateado ejemplares"
                            binding.grafRadar.description.text = "Distribución comparativa entre especies"
                            binding.grafRadar.description.textSize = 14f

                            val entradaGlobulus: ArrayList<RadarEntry> = ArrayList()
                            for(num in 10..19) {
                                entradaGlobulus.add(RadarEntry( unidadesPorDiametro.get(num).toFloat()))
                            }
                            val radarDataSet2 = RadarDataSet(entradaGlobulus, "Eucalytus Globulus")
                            radarDataSet2.color = resources.getColor(R.color.seleccion_verde)
                            radarDataSet2.lineWidth = 2f
                            radarDataSet2.setValueTextColor(resources.getColor(R.color.seleccion_verde))
                            radarDataSet2.valueTextSize = 14f
                            radarDataSet2.setDrawFilled(true)
                            radarDataSet2.fillColor = resources.getColor(R.color.seleccion_verde)
                            radarData.addDataSet(radarDataSet2)
                        }
                    }


                    /*
                    entries.add(RadarEntry(5f, unidadesPorDiametro.get(0).toFloat()))
                    entries.add(RadarEntry(10f, unidadesPorDiametro.get(1).toFloat()))
                    entries.add(RadarEntry(15f, unidadesPorDiametro.get(2).toFloat()))
                    entries.add(RadarEntry(20f, unidadesPorDiametro.get(3).toFloat()))
                    entries.add(RadarEntry(25f, unidadesPorDiametro.get(4).toFloat()))
                    entries.add(RadarEntry(30f, unidadesPorDiametro.get(5).toFloat()))
                    entries.add(RadarEntry(35f, unidadesPorDiametro.get(6).toFloat()))
                    entries.add(RadarEntry(40f, unidadesPorDiametro.get(7).toFloat()))
                    entries.add(RadarEntry(45f, unidadesPorDiametro.get(8).toFloat()))
                    entries.add(RadarEntry(50f, unidadesPorDiametro.get(9).toFloat()))

                     */

                    val etiqueta: String
                    val color: Int

                    if (especie == "Eucalyptus Globulus"){
                        etiqueta = "Eucalyptus Globulus"
                        color = resources.getColor(R.color.seleccion_verde)
                    }else{
                        etiqueta = "Eucalyptus Grandis"
                        color = resources.getColor(R.color.seleccion_azul)
                    }

                    val radarDataSet = RadarDataSet(entries, etiqueta)
                    radarDataSet.setDrawFilled(true)
                    if (etiqueta != "Eucalyptus Globulus"){
                        radarDataSet.fillColor = resources.getColor(R.color.seleccion_azul)
                    }

                    radarDataSet.color = color
                    radarDataSet.lineWidth = 2f
                    radarDataSet.setValueTextColor(color)
                    radarDataSet.valueTextSize = 14f

                    radarData.addDataSet(radarDataSet)

                    val etiquetas = arrayOf(
                        "1 a 5 cm",
                        "6 a 10 cm",
                        "11 a 15 cm",
                        "16 a 20 cm",
                        "21 a 25 cm",
                        "26 a 30 cm",
                        "31 a 35 cm",
                        "36 a 40 cm",
                        "41 a 45 cm",
                        "46 a 50 cm")

                    binding.grafRadar.xAxis.valueFormatter = IndexAxisValueFormatter(etiquetas)
                    binding.grafRadar.xAxis.textSize = 15f

                    if (especie == "AMBOS"){
                        binding.grafRadar.description.setPosition(935f, 1050f)
                    }else{
                        binding.grafRadar.description.setPosition(850f, 1050f)
                    }

                    binding.grafRadar.description.textSize = 18f

                    binding.grafRadar.animateX(1000)
                    binding.grafRadar.data = radarData

                }

            }

        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraficoDiametrosPorIndustriaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}