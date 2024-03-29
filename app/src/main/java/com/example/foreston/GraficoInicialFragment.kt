package com.example.foreston

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foreston.databinding.FragmentGraficoInicialBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


private const val ARG_PARAM0 = "TIPO_GRAFICO"
private const val ARG_PARAM1 = "ARBOLES_TOTALES"
private const val ARG_PARAM2 = "HECTAREAS_TOTALES"
private const val ARG_PARAM3 = "ASERRADERO_TOTALES"
private const val ARG_PARAM4 = "CELULOSA_TOTALES"
private const val ARG_PARAM5 = "PAPEL_TOTALES"
private const val ARG_PARAM6 = "SUBPRODUCTOS_TOTALES"
private const val ARG_PARAM7 = "INDUSTRIAS_VALORES"

class GraficoInicialFragment : Fragment() {

    private var _binding: FragmentGraficoInicialBinding? = null
    private val binding get() = _binding!!

    private var tipoGrafico: String? = ""
    private var arbolesTotales: Int? = 0
    private var hectareasTotales: Int? = 0
    private var pesoTotales: Double? = 0.0
    private var volumenTotales: Double? = 0.0
    private var carbonoTotales: Double? = 0.0
    private var rentabilidadTotales: Double? = 0.0
    private var aserraderoTotal: Double? = 0.0
    private var celulosaTotal: Double? = 0.0
    private var papelTotal: Double? = 0.0
    private var subproductosTotal: Double? = 0.0
    private var listaIndustrias = doubleArrayOf()

    private var listener: OnFragmentGraphicListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tipoGrafico = it.getString(ARG_PARAM0)
            arbolesTotales = it.getInt(ARG_PARAM1)
            hectareasTotales = it.getInt(ARG_PARAM2)
            aserraderoTotal = it.getDouble(ARG_PARAM3)
            celulosaTotal = it.getDouble(ARG_PARAM4)
            papelTotal = it.getDouble(ARG_PARAM5)
            subproductosTotal = it.getDouble(ARG_PARAM6)
            listaIndustrias = it.getDoubleArray(ARG_PARAM7)!!

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraficoInicialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.takeIf { it.containsKey(ARG_PARAM0) }?.apply {

            when (tipoGrafico){
                "INDUSTRIAS_PORCENTAJE" -> {

                    binding.grafIndustrias.isDrawHoleEnabled = true
                    binding.grafIndustrias.setUsePercentValues(true)
                    binding.grafIndustrias.setEntryLabelTextSize(12f)
                    binding.grafIndustrias.setEntryLabelColor(resources.getColor(R.color.black))
                    binding.grafIndustrias.setCenterText("Rentabilidad por Industria")
                    binding.grafIndustrias.setCenterTextSize(20f)
                    binding.grafIndustrias.getDescription().setEnabled(false)

                    val l: Legend = binding.grafIndustrias.getLegend()
                    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                    l.orientation = Legend.LegendOrientation.VERTICAL
                    l.setDrawInside(false)
                    l.isEnabled = true
                    l.textSize = 14f

                    val entries: ArrayList<PieEntry> = ArrayList()
                    entries.add(PieEntry(listaIndustrias.get(0).toFloat(), "Aserr. en Monte en Pie"))
                    entries.add(PieEntry(listaIndustrias.get(1).toFloat(), "Aserr. en Playa de Monte"))
                    entries.add(PieEntry(listaIndustrias.get(2).toFloat(), "Aserr. en Planta industrial"))
                    entries.add(PieEntry(listaIndustrias.get(3).toFloat(), "Celulosa"))
                    entries.add(PieEntry(listaIndustrias.get(4).toFloat(), "Papelera"))
                    entries.add(PieEntry(listaIndustrias.get(5).toFloat(), "SubProductos"))
                    entries.add(PieEntry(listaIndustrias.get(6).toFloat(), "Varas y Postes"))

                    val colors: ArrayList<Int> = ArrayList()
                    for (color in ColorTemplate.MATERIAL_COLORS) {
                        colors.add(color)
                    }

                    for (color in ColorTemplate.VORDIPLOM_COLORS) {
                        colors.add(color)
                    }

                    val dataSet = PieDataSet(entries, "Industrias")
                    dataSet.colors = colors

                    val data = PieData(dataSet)
                    data.setDrawValues(true)
                    data.setValueFormatter(PercentFormatter(binding.grafIndustrias))
                    data.setValueTextSize(12f)
                    data.setValueTextColor( resources.getColor(R.color.black))
                    binding.grafIndustrias.setData(data)
                    binding.grafIndustrias.invalidate()
                    binding.grafIndustrias.animateY(1400, Easing.EaseInOutQuad)


                }
                "INDUSTRIAS_VALOR" -> {
                    binding.grafIndustrias.isDrawHoleEnabled = true
                    binding.grafIndustrias.setEntryLabelTextSize(12f)
                    binding.grafIndustrias.setEntryLabelColor(resources.getColor(R.color.black))
                    binding.grafIndustrias.setCenterText("Rentabilidad por Industria")
                    binding.grafIndustrias.setCenterTextSize(20f)
                    binding.grafIndustrias.getDescription().setEnabled(false)

                    val l: Legend = binding.grafIndustrias.getLegend()
                    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                    l.orientation = Legend.LegendOrientation.VERTICAL
                    l.setDrawInside(false)
                    l.isEnabled = true
                    l.textSize = 14f

                    val entries: ArrayList<PieEntry> = ArrayList()
                    entries.add(PieEntry(listaIndustrias.get(0).toFloat(), "Aserr. en Monte en Pie"))
                    entries.add(PieEntry(listaIndustrias.get(1).toFloat(), "Aserr. en Playa de Monte"))
                    entries.add(PieEntry(listaIndustrias.get(2).toFloat(), "Aserr. en Planta industrial"))
                    entries.add(PieEntry(listaIndustrias.get(3).toFloat(), "Celulosa"))
                    entries.add(PieEntry(listaIndustrias.get(4).toFloat(), "Papelera"))
                    entries.add(PieEntry(listaIndustrias.get(5).toFloat(), "SubProductos"))
                    entries.add(PieEntry(listaIndustrias.get(6).toFloat(), "Varas y Postes"))

                    val colors: ArrayList<Int> = ArrayList()
                    for (color in ColorTemplate.MATERIAL_COLORS) {
                        colors.add(color)
                    }

                    for (color in ColorTemplate.VORDIPLOM_COLORS) {
                        colors.add(color)
                    }

                    val dataSet = PieDataSet(entries, "Industrias")
                    dataSet.colors = colors

                    val data = PieData(dataSet)
                    data.setDrawValues(true)
                    data.setValueFormatter(PercentFormatter(binding.grafIndustrias))
                    data.setValueTextSize(12f)
                    data.setValueTextColor( resources.getColor(R.color.black))

                    binding.grafIndustrias.setData(data)
                    binding.grafIndustrias.invalidate()
                    binding.grafIndustrias.animateY(1400, Easing.EaseInOutQuad)
                }
                "BARRAS_VALOR" -> {

                }

            }

        }
        // Dejo creado un listener por si quiero mandar info del fragment a la activity
        //binding.button2.setOnClickListener { listener?.onClickFragmentButton() }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentGraphicListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    /*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GraficoInicialFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraficoInicialFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    */
}