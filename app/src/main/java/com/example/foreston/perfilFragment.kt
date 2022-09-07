package com.example.foreston

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
import com.google.firebase.firestore.FirebaseFirestore


class perfilFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding =  FragmentPerfilBinding.inflate(inflater, container, false)

        val context1 = binding.CorreoDato.context
        //val context1 = binding.Getbutton.context

        val prefs = context1.getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
        var email = prefs.getString("email",null)

        if(email != null) {
            binding.CorreoDato.text = email.toString()
        }else{

            println("viene NUUUUUUUUUUUUUUUUUUUUUUUUUUUUULLL")
             email = prefs.getString("Email",null)
            binding.CorreoDato.text = email.toString()
        }


        binding.Savebutton.setOnClickListener {
            if (email != null) {
                db.collection("users").document(email).set(hashMapOf("nombre" to binding.NombreDato.text.toString(),
                    "apellido" to binding.ApellidoDato.text.toString(),
                    "direccion" to binding.DireccionDato.text.toString(),
                    "telefono" to binding.TelefonoDato.text.toString()))



            }else{
                println("email nulo")}
        }

        binding.Getbutton.setOnClickListener {
            if (email != null) {

                db.collection("users").document(email).get().addOnSuccessListener {
                    binding.NombreDato.setText(it.get("nombre")as String?)
                    binding.ApellidoDato.setText(it.get("apellido")as String?)
                    binding.DireccionDato.setText(it.get("direccion")as String?)
                    binding.TelefonoDato.setText(it.get("telefono")as String?)
                }
            }
        }

        return binding.root
    }





}