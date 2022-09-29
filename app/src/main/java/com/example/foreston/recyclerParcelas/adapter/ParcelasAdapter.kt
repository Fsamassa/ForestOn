package com.example.foreston.recyclerParcelas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foreston.R
import com.example.foreston.recyclerAsociados.adapter.AsociadoViewHolder
import com.example.foreston.recyclerParcelas.Parcela
import com.google.firebase.storage.FirebaseStorage

class ParcelasAdapter(private val parcelaList: ArrayList<Parcela>) : RecyclerView.Adapter<ParcelaViewHolder>(){

    private val dbS = FirebaseStorage.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ParcelaViewHolder(layoutInflater.inflate(R.layout.item_parcela, parent, false))
    }

    override fun onBindViewHolder(holder: ParcelaViewHolder, position: Int) {
        val item = parcelaList[position]
        holder.render(item,dbS)
    }

    override fun getItemCount(): Int {
        return parcelaList.size
    }

}