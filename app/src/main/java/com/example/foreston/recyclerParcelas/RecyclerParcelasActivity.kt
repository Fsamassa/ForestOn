package com.example.foreston.recyclerParcelas

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foreston.R
import com.example.foreston.databinding.ActivityRecyclerParcelasBinding
import com.example.foreston.recyclerParcelas.adapter.ParcelasAdapter
import com.example.foreston.utils.GeneralUtils
import com.google.firebase.firestore.*

class RecyclerParcelasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerParcelasBinding
    private lateinit var parcelaAdapter : ParcelasAdapter
    private lateinit var parcelarrayList : ArrayList<Parcela>
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerParcelasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerParce.layoutManager = LinearLayoutManager(this)
        binding.recyclerParce.setHasFixedSize(true)

        parcelarrayList = arrayListOf()
        parcelaAdapter = ParcelasAdapter(parcelarrayList, this)

        binding.recyclerParce.adapter = parcelaAdapter

        EventChangeLister()

    }

    private fun EventChangeLister(){

        val prefs = this.getSharedPreferences(
            getString(R.string.archivo_preferencias),
            Context.MODE_PRIVATE
        )
        val email = prefs.getString("Email", null)

        db = FirebaseFirestore.getInstance()
        db.collection("users").document(email!!).collection("parcelas")
            .addSnapshotListener(object: EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null){
                        Log.e("Firestore Error",error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            parcelarrayList.add(dc.document.toObject(Parcela::class.java))

                        }
                    }
                    parcelaAdapter.notifyDataSetChanged()
                }
            })
    }
    fun finishMe() { finish() }
}