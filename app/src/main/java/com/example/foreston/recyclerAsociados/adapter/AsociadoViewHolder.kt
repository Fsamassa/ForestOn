package com.example.foreston.recyclerAsociados.adapter

import android.graphics.BitmapFactory
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.foreston.R
import com.example.foreston.databinding.ItemAsociadoBinding
import com.example.foreston.recyclerAsociados.Asociado
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class AsociadoViewHolder(view: View):RecyclerView.ViewHolder(view){

    val binding = ItemAsociadoBinding.bind(view)
    private lateinit var storage: StorageReference

    fun render(asociadoModel: Asociado, dbStorage: FirebaseStorage){
        val imagen_url = asociadoModel.imagen_foto_url

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
        }

        binding.tvNombre.text = asociadoModel.nombre
        binding.tvApellido.text = asociadoModel.apellido
        binding.tvDomicilio.text = asociadoModel.direccion
        binding.tvCantCampos.text = asociadoModel.campos.toString()
        binding.tvEmail.text = asociadoModel.email
        binding.tvTelefono.text = asociadoModel.telefono

        binding.ivAsociadoFoto.setOnClickListener{
            Toast.makeText(binding.ivAsociadoFoto.context, "Quieres conectar con "+asociadoModel.nombre+" ?", Toast.LENGTH_SHORT).show()

            // Acá iria logica para que se conecten y se asocien, mostrar algun grafico o dato de ejemplo si se junta los campos de ambos.

        }
    }

}