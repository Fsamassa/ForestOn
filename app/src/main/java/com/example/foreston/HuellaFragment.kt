package com.example.foreston

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.foreston.databinding.FragmentHuellaBinding
import com.example.foreston.utils.GeneralUtils
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM0 = "EMAIL_USUARIO"


class HuellaFragment : Fragment() {

    private var _binding: FragmentHuellaBinding? = null
    private val binding get() = _binding!!

    private var emailUsuario: String? = ""
    private val db  = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            emailUsuario = it.getString(ARG_PARAM0)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHuellaBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.takeIf { it.containsKey(ARG_PARAM0) }?.apply {

                db.collection("users").document(emailUsuario!!).collection("parcelas").get()
                    .addOnSuccessListener { task ->

                        var carbonoTotal = 0.0
                        var edadTotal = 0
                        var cantParcelas = 0

                        val context = binding.vTituloBonos.context

                        for (document in task) {

                            cantParcelas++

                            val tablaContenedora = TableRow(context)
                            tablaContenedora.setPadding(15, 5, 15, 5)

                            val columna1 = TextView(context)
                            columna1.text = document.data.get("nombre_parcela").toString()
                            columna1.textSize = 15.0F
                            columna1.typeface = Typeface.DEFAULT_BOLD
                            tablaContenedora.addView(columna1)

                            val columna2 = TextView(context)
                            val carbono = document.data.get("carbono_total").toString()
                            columna2.text = "${GeneralUtils.formatearDecimales(carbono.toDouble())} ton"
                            columna2.textSize = 15.0F
                            columna2.gravity = Gravity.CENTER
                            tablaContenedora.addView(columna2)

                            carbonoTotal += carbono.toDouble()

                            val columna3 = TextView(context)
                            val edadArbol = document.data.get("edad").toString()

                            edadTotal += edadArbol.toInt()

                            columna3.text = "${GeneralUtils.pasarEdadanios(edadArbol)}"
                            columna3.textSize = 15.0F
                            columna3.gravity = Gravity.CENTER
                            tablaContenedora.addView(columna3)

                            binding.tlBonosParcelas.addView(tablaContenedora)
                        }

                        val tablaContenedora = TableRow(context)
                        tablaContenedora.setPadding(15,20,15,5)

                        val columnaTotal  = TextView(context)
                        columnaTotal.text = "TOTAL"
                        columnaTotal.textSize = 20.0F
                        columnaTotal.typeface = Typeface.DEFAULT_BOLD
                        columnaTotal.setTextColor(resources.getColor(R.color.totales_azul))
                        tablaContenedora.addView(columnaTotal)

                        val totalCarbono  = TextView(context)
                        totalCarbono.text = "${GeneralUtils.formatearNumerosGrandes(carbonoTotal)} ton"
                        totalCarbono.textSize = 20.0F
                        totalCarbono.gravity = Gravity.CENTER
                        totalCarbono.typeface = Typeface.DEFAULT_BOLD
                        columnaTotal.setTextColor(resources.getColor(R.color.totales_azul))
                        tablaContenedora.addView(totalCarbono)

                        binding.tlBonosParcelas.addView(tablaContenedora)

                        val tablaContenedora2 = TableRow(context)
                        tablaContenedora2.setPadding(15,20,15,5)

                        val columnaPromedio  = TextView(context)
                        columnaPromedio.text = "Promedio Edades"
                        columnaPromedio.textSize = 20.0F
                        columnaPromedio.typeface = Typeface.DEFAULT_BOLD
                        columnaPromedio.setTextColor(resources.getColor(R.color.totales_azul))
                        tablaContenedora2.addView(columnaPromedio)

                        val promedioEdad  = TextView(context)
                        val promedio = edadTotal / cantParcelas
                        promedioEdad.text = "${GeneralUtils.pasarEdadanios(promedio.toString())}"
                        promedioEdad.textSize = 20.0F
                        promedioEdad.gravity = Gravity.CENTER
                        promedioEdad.typeface = Typeface.DEFAULT_BOLD
                        columnaTotal.setTextColor(resources.getColor(R.color.totales_azul))
                        tablaContenedora2.addView(promedioEdad)

                        binding.tlBonosParcelas.addView(tablaContenedora2)

                    }
        }
    }
}