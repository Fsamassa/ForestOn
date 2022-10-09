package com.example.foreston.recyclerParcelas.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.Settings.Global.getString
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.foreston.R
import com.example.foreston.R.*
import com.example.foreston.databinding.ItemAsociadoBinding
import com.example.foreston.databinding.ItemParcelaBinding
import com.example.foreston.recyclerAsociados.Asociado
import com.example.foreston.recyclerParcelas.Parcela
import com.example.foreston.recyclerParcelas.Parcela_editarActivity
import com.example.foreston.recyclerParcelas.RecyclerParcelasActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ParcelaViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val binding = ItemParcelaBinding.bind(view)
    private lateinit var storage: StorageReference


    fun render(parcelaModel: Parcela, dbStorage: FirebaseStorage){
    /*    val imagen_url = itemModel.imagen_foto_url

        if (imagen_url != ""){
            storage = dbStorage.getReference("Users/" + imagen_url)
        }else{
            storage = dbStorage.getReference("Users/perfil_generico_3.png")
        }

      val localfile= File.createTempFile("tempImage","jpg")
        storage.getFile(localfile).addOnSuccessListener {
            val bitmap= BitmapFactory.decodeFile(localfile.absolutePath)
            binding.ivAsociadoFoto.setImageBitmap(bitmap)
            binding.progressWheel.isGone = true
        }*/

        binding.tvNombre.text = parcelaModel.nombre_parcela.toString()
        binding.tvaltura.text = parcelaModel.altura_prom.toString()
        binding.tvdiametro.text = parcelaModel.diametro_arboles.toString()
        binding.tvcantArb.text = parcelaModel.cant_arboles.toString()
        binding.tipo.text = parcelaModel.tipo.toString()
        binding.edadArb.text = parcelaModel.edad.toString()
        binding.DirePa.text = parcelaModel.direccion.toString()
        binding.tipoIndustria.text = parcelaModel.tipo_industria.toString()


            binding.detalle.setOnClickListener{
          //  Toast.makeText(binding.detalle.context, "en un rato veras los detalles", Toast.LENGTH_SHORT).show()
            val context1 = binding.DirePa.context
            val intento2=Intent(context1, Parcela_editarActivity::class.java)
                intento2.putExtra("parcela_name",binding.tvNombre.text.toString())
                println(binding.tvNombre.text.toString())
            context1.startActivity(intento2)


            // Acá iria logica para que se conecten y se asocien, mostrar algun grafico o dato de ejemplo si se junta los campos de ambos.

        }
    }


}