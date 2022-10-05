package com.example.foreston.recyclerParcelas

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foreston.InformacionActivity
import com.example.foreston.R
import com.example.foreston.databinding.ActivityEditarParcelaBinding
import com.example.foreston.databinding.ActivityParcelasBinding
import com.google.firebase.firestore.FirebaseFirestore

class Parcela_editarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarParcelaBinding
    private val db = FirebaseFirestore.getInstance()
    var nombre_parcela: String? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarParcelaBinding.inflate(layoutInflater)

        cargarnombreParcela()

        val pref=this.getSharedPreferences(getString(R.string.archivo_preferencias),
            Context.MODE_PRIVATE)

        var email = pref.getString("Email", null)


        db.collection("users").document(email!!).collection("parcelas").get().addOnSuccessListener { task ->

            for (document in task){

                db.collection("users").document(email).collection("parcelas").document(document.id.toString())
                    .get().addOnSuccessListener {

                        if(nombre_parcela==it.get("nombre_parcela")as String?){
                              if(nombre_parcela!=null){
                                if (email != null) {
                                    db.collection("users").document(email).collection("parcelas").document(document.id.toString())
                                        .get().addOnSuccessListener {

                                            binding.ENombreParcela.setText(it.get("nombre_parcela")as String?)
                                            binding.Etipodearbol.setText(it.get("tipo")as String?)
                                            binding.Eedad.setText(it.get("edad")as String?)
                                            binding.EdireParcela.setText(it.get("direccion")as String?)
                                            binding.Ediametro.setText(it.get("diametro_arboles")as String?)
                                            binding.EcantArboles.setText(it.get("cant_arboles")as String?)
                                            binding.Ealtura.setText(it.get("altura_prom")as String?)
                                        }
                                }
                            }

                        }
                    }


            }
        }

        binding.guardarParcela.setOnClickListener {

            if(binding.ENombreParcela.text.toString()!="" && binding.EdireParcela.text.toString()!=""
                && binding.EcantArboles.text.toString()!=""
                && binding.Ediametro.text.toString()!=""
                && binding.Ealtura.text.toString()!=""
                && binding.Etipodearbol.text.toString()!=""
                && binding.Eedad.text.toString()!=""){

                db.collection("users").document(email!!).collection("parcelas").get().addOnSuccessListener { task ->

                    for (document in task){
                        db.collection("users").document(email).collection("parcelas").document(document.id.toString())
                            .get().addOnSuccessListener {

                                if(nombre_parcela==it.get("nombre_parcela")as String?){

                                    db.collection("users").document(email!!).collection("parcelas").document(document.id.toString()).update(
                                        "altura_prom",binding.Ealtura.text.toString(),
                                        "cant_arboles" , binding.EcantArboles.text.toString(),
                                        "diametro_arboles" , binding.Ediametro.text.toString(),
                                        "tipo", binding.Etipodearbol.text.toString(),
                                        "edad" , binding.Eedad.text.toString(),
                                                "direccion",binding.EdireParcela.text.toString())

                                    Toast.makeText(this, "Datos Actualizados", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, RecyclerParcelasActivity::class.java)
                                    startActivity(intent)
                                }

                            }

                    }


                }
            }
        }


        setContentView(binding.root)
    }

    fun cargarnombreParcela(){
        val bundle= intent.extras
        nombre_parcela= bundle?.get("parcela_name") as String?

    }

}