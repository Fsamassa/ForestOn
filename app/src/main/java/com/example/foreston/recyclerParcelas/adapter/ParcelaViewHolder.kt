package com.example.foreston.recyclerParcelas.adapter


import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.foreston.ParcelaActivity
import com.example.foreston.databinding.ItemParcelaBinding
import com.example.foreston.recyclerParcelas.Parcela

class ParcelaViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val binding = ItemParcelaBinding.bind(view)

    fun render(parcelaModel: Parcela, activity : Activity){

        val nombreEditado = parcelaModel.nombre_parcela.toString()
        binding.etNombre.text = "\"$nombreEditado\""
        binding.etHectareas.text = parcelaModel.hectareas.toString()
        binding.etCantArboles.text = parcelaModel.cant_arboles.toString()
        binding.etEspecie.text = parcelaModel.tipo.toString()
        binding.etIndustriaDestino.text = parcelaModel.tipo_industria.toString()

        binding.etIndustriaDestino.isSelected = true
        binding.etNombre.isSelected = true

        val context = binding.etNombre.context

        binding.btnDetalles.setOnClickListener{
            val intent = Intent(context, ParcelaActivity::class.java)
            intent.putExtra("nombre_parcela", parcelaModel.nombre_parcela.toString())
            context.startActivity(intent)
            activity.finish()
        }
    }


}