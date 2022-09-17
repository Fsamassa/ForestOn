package com.example.foreston.recyclerAsociados

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foreston.databinding.ActivityRecyclerAsociadosBinding
import com.example.foreston.recyclerAsociados.adapter.AsociadoAdapter
import com.google.firebase.firestore.*

class RecyclerAsociadosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerAsociadosBinding
    private lateinit var asociadosAdapter : AsociadoAdapter
    private lateinit var asociadosArrayList : ArrayList<Asociado>
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerAsociadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerAsociados.layoutManager = LinearLayoutManager(this)
        binding.recyclerAsociados.setHasFixedSize(true)

        asociadosArrayList = arrayListOf()
        asociadosAdapter = AsociadoAdapter(asociadosArrayList)

        binding.recyclerAsociados.adapter = asociadosAdapter

        EventChangeLister()
    }

    private fun EventChangeLister(){
        db = FirebaseFirestore.getInstance()
        db.collection("users")
            .addSnapshotListener(object: EventListener<QuerySnapshot>{
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null){
                        Log.e("Firestore Error",error.message.toString())
                        return
                    }
                    for (dc:DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            asociadosArrayList.add(dc.document.toObject(Asociado::class.java))
                        }
                    }
                    asociadosAdapter.notifyDataSetChanged()
                }
            })
    }
}

