package com.example.foreston

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.example.foreston.databinding.FragmentGraficoDiametrosBinding
import com.example.foreston.databinding.FragmentGraficoInicialBinding
import com.example.foreston.utils.GeneralUtils
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

private const val ARG_PARAM0 = "TIPO_GRAFICO"
private const val ARG_PARAM1 = "ARBOLES_TOTALES"
private const val ARG_PARAM2 = "LISTA_DIAMETROS"
private const val ARG_PARAM3 = "ESPECIE"


class GraficoDiametrosFragment : Fragment() {

    private var _binding: FragmentGraficoDiametrosBinding? = null
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
        _binding = FragmentGraficoDiametrosBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.takeIf { it.containsKey(ARG_PARAM0) }?.apply {

            var totalEspecie = 0f

            for(num in 0..49) {
                totalEspecie += unidadesPorDiametro.get(num).toFloat()
            }
            val totalEspecieFormateado = GeneralUtils.formatearNumerosGrandes(totalEspecie.toDouble())
            val arbolesTotalesFormateado = GeneralUtils.formatearNumerosGrandes(arbolesTotales!!.toDouble())

            when (tipoGrafico){
                "BARRAS_VALOR" -> {
                    when (especie){
                        "Eucalyptus Grandis" -> {
                            binding.tvCantArboles.text =
                                "Distribución de $totalEspecieFormateado \n ejemplares de $especie"
                            binding.tvEscala.text =
                                " $totalEspecieFormateado de $arbolesTotalesFormateado ejemplares totales"
                        }
                        "Eucalyptus Globulus" -> {
                            binding.tvCantArboles.text =
                                "Distribución de $totalEspecieFormateado \n ejemplares de $especie"
                            binding.tvEscala.text =
                                " - $totalEspecieFormateado de $arbolesTotalesFormateado ejemplares totales"
                        }
                        "TODOS" -> {
                            binding.tvCantArboles.text = "Distribución general de $arbolesTotalesFormateado ejemplares"
                            binding.tvEscala.visibility = GONE

                        }
                    }

                    val entries: ArrayList<BarEntry> = ArrayList()
                    /*
                    entries.add(BarEntry(5f, unidadesPorDiametro.get(0).toFloat()))
                    entries.add(BarEntry(10f, unidadesPorDiametro.get(1).toFloat()))
                    entries.add(BarEntry(15f, unidadesPorDiametro.get(2).toFloat()))
                    entries.add(BarEntry(20f, unidadesPorDiametro.get(3).toFloat()))
                    entries.add(BarEntry(25f, unidadesPorDiametro.get(4).toFloat()))
                    entries.add(BarEntry(30f, unidadesPorDiametro.get(5).toFloat()))
                    entries.add(BarEntry(35f, unidadesPorDiametro.get(6).toFloat()))
                    entries.add(BarEntry(40f, unidadesPorDiametro.get(7).toFloat()))
                    entries.add(BarEntry(45f, unidadesPorDiametro.get(8).toFloat()))
                    entries.add(BarEntry(50f, unidadesPorDiametro.get(9).toFloat()))

                     */
                    for(num in 0..49) {
                        entries.add(BarEntry(num.toFloat() + 1, unidadesPorDiametro.get(num).toFloat()))
                    }

                    val barDataSet = BarDataSet(entries, "Unidades por diametro")

                    val colors: ArrayList<Int> = ArrayList()
                    for (color in ColorTemplate.MATERIAL_COLORS) {
                        colors.add(color)
                    }
                    barDataSet.colors = colors
                    barDataSet.valueTextColor = resources.getColor(R.color.seleccion_azul)
                    barDataSet.setValueTextSize(18f)

                    val barData : BarData = BarData(barDataSet)

                    binding.grafBar.setFitBars(true)
                    binding.grafBar.data = barData
                    binding.grafBar.description.text = "Ejemplares por diametro"
                    binding.grafBar.description.textSize = 14f

                    binding.grafBar.animateY(2000)

                }

            }

        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GraficoDiametrosFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraficoDiametrosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}