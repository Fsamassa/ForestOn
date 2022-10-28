package com.example.foreston.recyclerParcelas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foreston.R
import com.example.foreston.recyclerParcelas.Parcela
import com.example.foreston.recyclerParcelas.RecyclerParcelasActivity

class ParcelasAdapter(private val parcelaList: ArrayList<Parcela>, context: Context) : RecyclerView.Adapter<ParcelaViewHolder>(){

    private val activity : RecyclerParcelasActivity = context as RecyclerParcelasActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ParcelaViewHolder(layoutInflater.inflate(R.layout.item_parcela, parent, false))
    }

    override fun onBindViewHolder(holder: ParcelaViewHolder, position: Int) {
        val item = parcelaList[position]
        holder.render(item, activity)
    }

    override fun getItemCount(): Int {
        return parcelaList.size
    }

}