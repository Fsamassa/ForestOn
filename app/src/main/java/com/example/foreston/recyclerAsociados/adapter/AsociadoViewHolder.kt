package com.example.foreston.recyclerAsociados.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.view.View
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.foreston.databinding.ItemAsociado2Binding
import com.example.foreston.recyclerAsociados.Asociado
import com.example.foreston.utils.GeneralUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File



class AsociadoViewHolder(view: View):RecyclerView.ViewHolder(view){

    //val binding = ItemAsociadoBinding.bind(view)
    val binding= ItemAsociado2Binding.bind(view)
    private lateinit var storage: StorageReference
    val  dialogo : Dialog? =null
    private val db2 = FirebaseFirestore.getInstance()



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

       /* binding.tvNombre.text = asociadoModel.nombre
        binding.tvApellido.text = asociadoModel.apellido
        binding.tvDomicilio.text = asociadoModel.direccion
        binding.tvCantCampos.text = asociadoModel.campos.toString()
        binding.tvEmail.text = asociadoModel.email
        binding.tvTelefono.text = asociadoModel.telefono*/


        val mail : String? = asociadoModel.email

        db2.collection("users").document(mail!!).collection("parcelas").get().addOnSuccessListener {
           binding.cantidadPValor.text=it.size().toString()
            var cant_totalCarbono:Double=0.0
            var cant_hectareas:Double=0.0

            for (doc in it.documents){

                if(doc.get("carbono_total")==null) {
                    cant_totalCarbono = cant_totalCarbono + 0
                }else{cant_totalCarbono = cant_totalCarbono + doc.get("carbono_total").toString().toDouble()}

                if(doc.get("hectareas")==null){
                    cant_hectareas=cant_hectareas+0
                }else{cant_hectareas=cant_hectareas+doc.get("hectareas").toString().toDouble()}
            }


            binding.co2consumido.text = GeneralUtils.formatearDecimales(cant_totalCarbono)
            binding.HAdeCampo.text=cant_hectareas.toString()
        }

        binding.ivAsociadoFoto.setOnClickListener{
            val dialogBuilder = AlertDialog.Builder(binding.cantidadPValor.context)
            dialogBuilder.setMessage("Datos de "+asociadoModel.nombre?.uppercase()+" "+asociadoModel.apellido?.uppercase()+"\n\nEmail: "+asociadoModel.email
            +"\nTeléfono : "+asociadoModel.telefono
            +"\nDirección: "+asociadoModel.direccion)
                .setCancelable(false)

                .setPositiveButton("ok", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
                })

            val alert = dialogBuilder.create()
            alert.show()
        }


    }

}