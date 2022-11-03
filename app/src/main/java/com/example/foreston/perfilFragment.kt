package com.example.foreston

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foreston.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import kotlin.math.roundToInt


class perfilFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var auten : FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var imagenUri : Uri
    private var imagen_url : String = ""
    companion object{
        val IMAGE_REQUEST_code = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding =  FragmentPerfilBinding.inflate(inflater, container, false)
        auten  = FirebaseAuth.getInstance()

        val context1 = binding.CorreoDato.context
        val prefs = context1.getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
        val email = prefs.getString(getString(R.string.Email),null)

        binding.CorreoDato.text = email.toString()
        // traer los datos de la base de datos

        db.collection("users").document(email!!).get().addOnSuccessListener {
            binding.NombreDato.setText(it.get("nombre")as String?)
            binding.ApellidoDato.setText(it.get("apellido")as String?)
            binding.DireccionDato.setText(it.get("direccion")as String?)
            binding.TelefonoDato.setText(it.get("telefono")as String?)
            //val cantCampos = (it.get("campos")) as Long
          //  binding.etCampos.setText(cantCampos.toString())
            imagen_url = (it.get("imagen_foto_url") as String)

            if (imagen_url != ""){
                //     storageReference = FirebaseStorage.getInstance().getReference("Users/"+auten.currentUser?.uid)
                storageReference = FirebaseStorage.getInstance().getReference("Users/"+imagen_url)
            }else{
                storageReference = FirebaseStorage.getInstance().getReference("Users/perfil_generico_3.png")
            }

            val localfile = File.createTempFile("tempImage","jpg")
            storageReference.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imagenPerfil.setImageBitmap(bitmap)
            }
            imagenUri = localfile.toUri()
        }

      /*      db.collection("users").document(email).collection("parcelas").get().addOnSuccessListener { task ->

                for (document in task){

                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            }}*/

        //fin de carga de datos

        binding.imagenPerfil.setOnClickListener {
            elegirImagen()
        }

        binding.Savebutton.setOnClickListener {
            if ( binding.ApellidoDato.text.toString() !=""
                && binding.NombreDato.text.toString() !=""
               // && binding.DireccionDato.text.toString() !=""   -> Les damos la posibilidad de que no carguen esos
              //  && binding.TelefonoDato.text.toString() !=""    -> campos si no quieren.
               // && binding.etCampos.text.toString() !=""  -> no hay campos
            )
            {
              //  val cantCampos = binding.etCampos.text.toString()

                db.collection("users").document(email).
                set(hashMapOf(
                    "nombre" to binding.NombreDato.text.toString(),
                    "apellido" to binding.ApellidoDato.text.toString(),
                    "direccion" to binding.DireccionDato.text.toString(),
                    "telefono" to binding.TelefonoDato.text.toString(),
                    "email" to email,
                   // "campos" to cantCampos.toLong(),
                    "imagen_foto_url" to auten.currentUser?.uid,
                ))

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

        storageReference = FirebaseStorage.getInstance().getReference("Users/"+auten.currentUser?.uid)
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