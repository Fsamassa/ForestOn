package com.example.foreston

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foreston.databinding.FragmentGraficoDiametrosPorIndustriaBinding
import com.example.foreston.databinding.FragmentGraficoRadarPorIndustriaBinding
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


private const val ARG_PARAM0 = "TIPO_GRAFICO"
private const val ARG_PARAM1 = "ARBOLES_TOTALES"
private const val ARG_PARAM2 = "INDUSTRIAS_VALORES"
private const val ARG_PARAM3 = "ESPECIE"

class GraficoRadarPorIndustriaFragment : Fragment() {
    private var _binding: FragmentGraficoRadarPorIndustriaBinding? = null
    private val binding get() = _binding!!

    private var tipoGrafico: String? = ""
    private var arbolesTotales: Int? = 0
    private var listaIndustrias = doubleArrayOf()
    private var especie : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tipoGrafico = it.getString(ARG_PARAM0)
            arbolesTotales = it.getInt(ARG_PARAM1)
            listaIndustrias = it.getDoubleArray(ARG_PARAM2)!!
            especie = it.getString(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraficoRadarPorIndustriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.takeIf { it.containsKey(ARG_PARAM0) }?.apply {

            val entries: ArrayList<RadarEntry> = ArrayList()
            val radarData : RadarData = RadarData()
            for(num in 0..6) {
                entries.add(RadarEntry( (listaIndustrias.get(num).toFloat())  ) )
            }

            when (tipoGrafico){
                "RADAR_INDUSTRIAS" -> {

/*
                    entries.add(RadarEntry(5f, listaIndustrias.get(0).toFloat()))
                    entries.add(RadarEntry(10f, listaIndustrias.get(1).toFloat()))
                    entries.add(RadarEntry(15f, listaIndustrias.get(2).toFloat()))
                    entries.add(RadarEntry(20f, listaIndustrias.get(3).toFloat()))
                    entries.add(RadarEntry(25f, listaIndustrias.get(4).toFloat()))
                    entries.add(RadarEntry(30f, listaIndustrias.get(5).toFloat()))
                    entries.add(RadarEntry(35f, listaIndustrias.get(6).toFloat()))
                    entries.add(RadarEntry(40f, listaIndustrias.get(7).toFloat()))
                    entries.add(RadarEntry(45f, listaIndustrias.get(8).toFloat()))
                    entries.add(RadarEntry(50f, listaIndustrias.get(9).toFloat()))

 */


                    val radarDataSet = RadarDataSet(entries, "Distribución económica")
                    radarDataSet.color = resources.getColor(R.color.blue_semi_transparent)
                    radarDataSet.lineWidth = 2f
                    radarDataSet.setDrawFilled(true)
                    radarDataSet.fillColor = resources.getColor(R.color.blue_semi_transparent)
                    radarDataSet.setValueTextColor(resources.getColor(R.color.seleccion_azul))
                    radarDataSet.valueTextSize = 14f

                    radarData.addDataSet(radarDataSet)

                    val etiquetas = arrayOf(
                        "As. Monte",
                        "As. Playa",
                        "As. Planta",
                        "Celulosa",
                        "Papel",
                        "Subprod.",
                        "Postes")

                    binding.grafRadar.description.text = "Distribución rentabilidad por Industria"
                    binding.grafRadar.description.setPosition(900f, 1200f)
                    binding.grafRadar.description.textSize = 18f

                    binding.grafRadar.xAxis.valueFormatter = IndexAxisValueFormatter(etiquetas)
                    binding.grafRadar.xAxis.textSize = 12f
                    binding.grafRadar.animateX(1000)
                    binding.grafRadar.data = radarData

                }

            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraficoRadarPorIndustriaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}