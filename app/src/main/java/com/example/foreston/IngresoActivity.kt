package com.example.foreston

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AlertDialog
import com.example.foreston.databinding.ActivityIngresoBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class IngresoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIngresoBinding

    private val respuestaLogueoGoogle = registerForActivityResult(StartActivityForResult()){ activityResult ->
        if (activityResult.resultCode == RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful){
                            val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).edit()
                            prefs.putString("email", binding.editTextEmail.text.toString())
                            prefs.apply()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }else{
                            mostrarAlerta("Logueo con cuenta GOOGLE fallida. Contactese con Admin.")
                        }
                    }
                }
            } catch (e: ApiException){
                mostrarAlerta("Error en la obtención de las credenciales de la Cuenta GOOGLE.")
            }
        }else{
            mostrarAlerta("Error buscando cuentas de GOOGLE. Contactese con Admin.")
        }
    }



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
        binding.buttonSignUp.setOnClickListener{
            if(binding.editTextEmail.text.isNotEmpty() && binding.editTextPassword.text.isNotEmpty()){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).edit()
                        prefs.putString("email", binding.editTextEmail.text.toString())
                        prefs.apply()
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
        binding.buttonGoogle.setOnClickListener {
            val googleConf =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            respuestaLogueoGoogle.launch(googleClient.signInIntent)

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