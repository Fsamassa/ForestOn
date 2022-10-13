package com.example.foreston

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.foreston.databinding.ActivityInformacionBinding
import com.example.foreston.databinding.ActivityParcelasBinding
import com.google.firebase.firestore.FirebaseFirestore


class ParcelaActivity : AppCompatActivity() {

     var maildeUsuario: String? =null
    private lateinit var binding: ActivityParcelasBinding
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParcelasBinding.inflate(layoutInflater)
        setContentView(binding.root)

       cargarmailenParcela()

        binding.guardarParcela.setOnClickListener {
        if(maildeUsuario!=null && binding.NombreParcela.text.toString()!="" && binding.direParcela.text.toString()!=""
            && binding.cantArboles.text.toString()!=""
            && binding.diametro.text.toString()!=""
            && binding.altura.text.toString()!=""
            && binding.tipodearbol.text.toString()!=""
            && binding.edad.text.toString()!=""
            && binding.etIndustria.text.toString()!=""
        ){
            db.collection("users").document(maildeUsuario!!).collection("parcelas").document(binding.NombreParcela.text.toString()).set(hashMapOf(
                "nombre_parcela" to binding.NombreParcela.text.toString(),
                "altura_prom" to binding.altura.text.toString(),
                "cant_arboles" to binding.cantArboles.text.toString(),
                "diametro_arboles" to binding.diametro.text.toString(),
                "tipo" to binding.tipodearbol.text.toString(),
                "edad" to binding.edad.text.toString(),
                "direccion" to binding.direParcela.text.toString(),
                "tipo_industria" to binding.etIndustria.text.toString()
                ))


            Toast.makeText(this, "Datos Actualizados", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, InformacionActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "completar todo los datos", Toast.LENGTH_SHORT).show()
        }
        }
    }

    fun cargarmailenParcela(){
        val bundle= intent.extras
         maildeUsuario= bundle?.get("email") as String?

        println("llego el mail a parcela"+maildeUsuario.toString())

    }
}