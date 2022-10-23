package com.example.foreston.recyclerAsociados.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foreston.R
import com.example.foreston.recyclerAsociados.Asociado
import com.google.firebase.storage.FirebaseStorage

class AsociadoAdapter(private val asociadosList:ArrayList<Asociado>) : RecyclerView.Adapter<AsociadoViewHolder>() {

    private val dbS = FirebaseStorage.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsociadoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AsociadoViewHolder(layoutInflater.inflate(R.layout.item_asociado_2, parent, false))
    }

    override fun onBindViewHolder(holder: AsociadoViewHolder, position: Int) {
        val item = asociadosList[position]
        holder.render(item,dbS)
    }

    override fun getItemCount(): Int {
        return asociadosList.size
    }
}