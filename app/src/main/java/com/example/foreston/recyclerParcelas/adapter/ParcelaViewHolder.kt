package com.example.foreston.recyclerParcelas.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.foreston.ParcelaActivity
import com.example.foreston.databinding.ItemParcelaBinding
import com.example.foreston.recyclerParcelas.Parcela
import com.example.foreston.recyclerParcelas.Parcela_editarActivity

class ParcelaViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val binding = ItemParcelaBinding.bind(view)

    fun render(parcelaModel: Parcela){

        binding.etNombre.text = parcelaModel.nombre_parcela.toString()
        binding.etHectareas.text = parcelaModel.hectareas.toString()
        binding.etCantArboles.text = parcelaModel.cant_arboles.toString()
        binding.etEspecie.text = parcelaModel.tipo.toString()
        binding.etIndustriaDestino.text = parcelaModel.tipo_industria.toString()

        binding.etIndustriaDestino.isSelected = true
        binding.etNombre.isSelected = true

        val context = binding.etNombre.context

        binding.btnEditar.setOnClickListener{

            val intent = Intent(context, Parcela_editarActivity::class.java)
                intent.putExtra("nombre_parcela",parcelaModel.nombre_parcela.toString())
                context.startActivity(intent)
        }

        binding.btnDetalles.setOnClickListener{
            val intent = Intent(context, ParcelaActivity::class.java)
            intent.putExtra("nombre_parcela",parcelaModel.nombre_parcela.toString())
            context.startActivity(intent)

        }
    }


}