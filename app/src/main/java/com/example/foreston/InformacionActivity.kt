package com.example.foreston

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import com.example.foreston.databinding.ActivityHomeBinding
import com.example.foreston.databinding.ActivityInformacionBinding
import com.example.foreston.databinding.FragmentPerfilBinding
import com.example.foreston.recyclerAsociados.RecyclerAsociadosActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text


class InformacionActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auten: FirebaseAuth
    private lateinit var binding: ActivityInformacionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auten = FirebaseAuth.getInstance()
        val uid = auten.currentUser?.uid

        val prefs = this.getSharedPreferences(
            getString(R.string.archivo_preferencias),
            Context.MODE_PRIVATE
        )
        var email = prefs.getString("Email", null)

        binding.mailInfoForestal.setText(email)


        binding.btncargarcampo.setOnClickListener {

            val intent = Intent(this, ParcelaActivity::class.java)
            intent.putExtra("email",binding.mailInfoForestal.text.toString())
            startActivity(intent)
            finish()
        }

        setuptable()


        /*   if (uid != null) {
            db.collection("users").document(uid.toString()).get().addOnSuccessListener {
                binding.mailInfoForestal.setText(it.get("email")as String?)

            }}else{
                println(uid.toString())
            }*/
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

                        val tv0=TextView(this)
                        tv0.text=document.data.get("nombre_parcela").toString().uppercase()
                        tv0.gravity=Gravity.CENTER
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