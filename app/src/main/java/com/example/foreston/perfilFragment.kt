package com.example.foreston

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewbinding.ViewBindings
import com.example.foreston.databinding.FragmentHomeBinding
import com.example.foreston.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ResultReceiver
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import com.facebook.login.widget.ProfilePictureView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File


class perfilFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var auten : FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var imagenUri : Uri

    // select imagen
    private lateinit var imageView: ImageView

    companion object{
        val IMAGE_REQUEST_code =100
            }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding =  FragmentPerfilBinding.inflate(inflater, container, false)

        auten  = FirebaseAuth.getInstance()
        val uid = auten.currentUser?.uid

        val context1 = binding.CorreoDato.context
        val prefs = context1.getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
        var email = prefs.getString("email",null)

        if(email != null) {
            binding.CorreoDato.text = email.toString()
        }else{

            println("viene NUUUUUUUUUUUUUUUUUUUUUUUUUUUUULLL")
             email = prefs.getString("Email",null)
            binding.CorreoDato.text = email.toString()
        }
        // traer los datos de la base de datos
        if (email != null) {
            db.collection("users").document(email).get().addOnSuccessListener {
                binding.NombreDato.setText(it.get("nombre")as String?)
                binding.ApellidoDato.setText(it.get("apellido")as String?)
                binding.DireccionDato.setText(it.get("direccion")as String?)
                binding.TelefonoDato.setText(it.get("telefono")as String?)
            }
        }

        storageReference=FirebaseStorage.getInstance().getReference("Users/"+auten.currentUser?.uid)
        val localfile=File.createTempFile("tempImage","jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap=BitmapFactory.decodeFile(localfile.absolutePath)
            binding.imagenPerfil.setImageBitmap(bitmap)
        }
        imagenUri=localfile.toUri()
        //fin de carga de datos

        binding.imagenPerfil.setOnClickListener {

            elegirImagen()

        }

        binding.Savebutton.setOnClickListener {
            if (email != null && binding.ApellidoDato.text.toString() !=""
                && binding.NombreDato.text.toString() !=""
                && binding.DireccionDato.text.toString() !=""
                && binding.TelefonoDato.text.toString() !="") {
                db.collection("users").document(email).
                set(hashMapOf(
                    "nombre" to binding.NombreDato.text.toString(),
                    "apellido" to binding.ApellidoDato.text.toString(),
                    "direccion" to binding.DireccionDato.text.toString(),
                    "telefono" to binding.TelefonoDato.text.toString()))

               // imagenUri=localfile.toUri()
                cargarImagen(imagenUri)

                Toast.makeText(activity, "Datos Actualizados", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(activity, "Completar todos los campos", Toast.LENGTH_SHORT).show() }
        }

      /*  binding.Getbutton.setOnClickListener {
            if (email != null) {
                db.collection("users").document(email).get().addOnSuccessListener {
                    binding.NombreDato.setText(it.get("nombre")as String?)
                    binding.ApellidoDato.setText(it.get("apellido")as String?)
                    binding.DireccionDato.setText(it.get("direccion")as String?)
                    binding.TelefonoDato.setText(it.get("telefono")as String?)
                }
            }
        }*/

        return binding.root
    }


    private fun elegirImagen() {
        val intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent, IMAGE_REQUEST_code)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== IMAGE_REQUEST_code){
           // imageView.setImageURI(data?.data)
            imagenUri=data?.data!!
            binding.imagenPerfil.setImageURI(imagenUri)
        }
    }

    private fun cargarImagen(imagenUri:Uri) {
        //imagenUri = Uri.parse("android.resource://com.example.foreston/drawable/app_foreston_mano")

        storageReference=FirebaseStorage.getInstance().getReference("Users/"+auten.currentUser?.uid)
        storageReference.putFile(imagenUri).addOnSuccessListener {
           println("subio foto")
        }.addOnFailureListener{
            println("fallo foto")
        }
    }

    private fun descargarImagen() {
        //imagenUri = Uri.parse("android.resource://com.example.foreston/drawable/app_foreston_mano")

        storageReference=FirebaseStorage.getInstance().getReference("Users/"+auten.currentUser?.uid)
        storageReference.putFile(imagenUri).addOnSuccessListener {
            println("subio foto")
        }.addOnFailureListener{
            println("fallo foto")
        }
    }

}