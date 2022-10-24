package com.example.foreston

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import com.example.foreston.databinding.ActivityInformacionBinding
import com.example.foreston.utils.GeneralUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class InformacionActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auten: FirebaseAuth
    private lateinit var binding: ActivityInformacionBinding
    private lateinit var item : String
    private lateinit var especie : String
    private lateinit var diametro : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auten = FirebaseAuth.getInstance()

        binding.btncargarcampo.setOnClickListener {
/*
            val intent = Intent(this, ParcelaActivity::class.java)
            intent.putExtra("email",binding.mailInfoForestal.text.toString())
            startActivity(intent)
            finish()
*/

            when (especie){
                "Eucalipto - Eucalyptus Grandis" -> {
                    val intent = Intent(this, InfoScaneadaActivity::class.java)
                    intent.putExtra("diametro", diametro.removeSuffix(" cm"))
                    intent.putExtra("especie", "Eucalyptus Grandis")
                    startActivity(intent)
                    finish()
                }
                "Eucalipto - Eucalyptus Globulus" -> {
                    val intent = Intent(this, InfoScaneadaActivity::class.java)
                    intent.putExtra("diametro", diametro.removeSuffix(" cm"))
                    intent.putExtra("especie", "Eucalyptus Globulus")
                    startActivity(intent)
                    finish()
                   }
                "Pino - Pinus ponderosa" -> {
                    GeneralUtils.mostrarAlerta(this,
                        "Lo sentimos, la especie $especie será implementada proximanente.\nPor favor elija otra de las disponibles",
                        null)

                }
                "Álamo - Populus Simonii" -> {
                    GeneralUtils.mostrarAlerta(this,
                        "Lo sentimos, la especie $especie será implementada proximanente.\nPor favor elija otra de las disponibles",
                        null)
                }
                else -> {
                    GeneralUtils.mostrarAlerta(this,
                        "Debes seleccionar una especie de árbol para continuar.",
                        null)
                }
            }

        }

        seleccionEspecieArbol()
        seleccionDiametroArbol()
        setuptable()

        /*   if (uid != null) {
            db.collection("users").document(uid.toString()).get().addOnSuccessListener {
                binding.mailInfoForestal.setText(it.get("email")as String?)

            }}else{
                println(uid.toString())
            }*/
    }

    private fun seleccionDiametroArbol() {
        val diametroString = resources.getStringArray(R.array.diametros_validos)

        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_item, diametroString)
        binding.acDiametros.setAdapter(arrayAdapter)

        with(binding.acDiametros){
            onItemClickListener = this@InformacionActivity
        }
    }

    private fun seleccionEspecieArbol() {
        val especieString = resources.getStringArray(R.array.especie_arbol)

        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_especie, especieString)
        binding.acEspecies.setAdapter(arrayAdapter)

        with(binding.acEspecies){
            onItemClickListener = this@InformacionActivity
        }
    }
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        item = parent!!.getItemAtPosition(position).toString()

        when (item){
            "Eucalipto - Eucalyptus Grandis" -> {
                binding.acEspecies.setTextColor(getColor(R.color.black))
                especie = "Eucalipto - Eucalyptus Grandis"
            }
            "Eucalipto - Eucalyptus Globulus" -> {
                binding.acEspecies.setTextColor(getColor(R.color.black))
                especie = "Eucalipto - Eucalyptus Globulus"
            }
            "Pino - Pinus ponderosa" -> {
                val mensajeToast = Toast.makeText(this, "Lo sentimos, especie proximanente ha ser implementada! \n Por favor elija otra de las disponibles",
                    Toast.LENGTH_LONG)
                mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                mensajeToast.show()
                binding.acEspecies.setTextColor(getColor(R.color.seleccion_rojo))
                especie = "Pino - Pinus ponderosa"
            }
            "Álamo - Populus Simonii" -> {
                val mensajeToast = Toast.makeText(this, "Lo sentimos, especie proximanente ha ser implementada! \n Por favor elija otra de las disponibles",
                    Toast.LENGTH_LONG)
                mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                mensajeToast.show()
                binding.acEspecies.setTextColor(getColor(R.color.seleccion_rojo))
                especie = "Álamo - Populus Simonii"
            }
            else -> {
                diametro = item
            }
        }


    }

    private fun setuptable() {

      /*  val tableRow0 = TableRow(this)
        val textView0 = TextView(this)
        textView0.text = "Nombre de Parcela  "
        textView0.setTextColor(Color.BLACK)

        tableRow0.addView(textView0)

        val textview1 = TextView(this)
        textview1.text = "   Detalle"
        textview1.setTextColor(Color.BLACK)
        tableRow0.addView(textview1)

        binding.tableLayout.addView(tableRow0)*/

        val prefs = this.getSharedPreferences(
            getString(R.string.archivo_preferencias),
            Context.MODE_PRIVATE
        )
        var email = prefs.getString("Email", null)

        if (email != null) {
            db.collection("users").document(email).collection("parcelas").get()
                .addOnSuccessListener { task ->

                    for (document in task) {

                        val tblrow0= TableRow(this)
                        tblrow0.setPadding(5,10,5,10)

                        val tv0 = TextView(this)
                        tv0.text = document.data.get("nombre_parcela").toString().uppercase()
                        tv0.gravity = Gravity.CENTER
                        tblrow0.addView(tv0)

                        val tv1=TextView(this)
                        tv1.text=document.data.get("direccion").toString()
                        tv1.gravity=Gravity.CENTER
                        tblrow0.addView(tv1)
                        binding.tableLayout.addView(tblrow0)
                        //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    }
                }

        }


    }

}