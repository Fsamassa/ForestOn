package com.example.foreston

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.foreston.databinding.ActivityIngresoBinding
import com.google.firebase.auth.FirebaseAuth

class IngresoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIngresoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngresoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logueo()
        sesion()

    }

    private fun sesion(){
        val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if(email != null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logueo() {
       binding.buttonSignUp.setOnClickListener{
           if(binding.editTextEmail.text.isNotEmpty() && binding.editTextPassword.text.isNotEmpty()){

               FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.editTextEmail.text.toString(),
                   binding.editTextPassword.text.toString()).addOnCompleteListener {
                       if(it.isSuccessful){
                           val intent = Intent(this, HomeActivity::class.java)
                           startActivity(intent)
                       }else{
                           mostrarAlerta("Creacion de usuario fallido. Contactese con Admin.")
                       }
               }
           }else{
               mostrarAlerta("Datos incompletos. No se admiten espacios en blanco.")
           }
       }
       binding.buttonLogin.setOnClickListener{
            if(binding.editTextEmail.text.isNotEmpty() && binding.editTextPassword.text.isNotEmpty()){

                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){

                        val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).edit()
                        prefs.putString("email", binding.editTextEmail.text.toString())
                        prefs.apply()

                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }else{
                        mostrarAlerta("Logueo fallido. Revise credenciales.")
                    }
                }
            }else{
                mostrarAlerta("Datos incompletos. No se admiten espacios en blanco.")
            }

        }

    }

    private fun mostrarAlerta(mensaje: String ){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}