package com.example.foreston

import android.graphics.Color
import android.Manifest
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foreston.databinding.ActivityHomeBinding
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.example.foreston.recyclerAsociados.RecyclerAsociadosActivity
import com.example.foreston.utils.UtilsAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var storage: StorageReference
    private val db  = FirebaseFirestore.getInstance()
    private val dbS = FirebaseStorage.getInstance()

    private val CAMERA_REQUEST_CODE = 0

    // urls test
    private final var INTA_URL: String = "https://inta.gob.ar/sites/default/files/inta_concordia_planilla_de_precios_forestales_julio_2021.pdf"
    private final var BONOS_URL: String = "http://www.ceads.org.ar/mercado-de-bonos-de-carbono-voluntario-vs-regulado/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = null

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerLayout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setBackgroundColor(Color.WHITE)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, HomeFragment())
        fragmentTransaction.commit()

        val headerView = navigationView.getHeaderView(0)

        val drawerImageUser = headerView.findViewById<ImageView>(R.id.drawerImageUser)
        val drawerNameUser = headerView.findViewById<TextView>(R.id.drawerNameUuser)
        val drawerLoggedUser = headerView.findViewById<TextView>(R.id.drawerLoggedUser)

        val prefsEmail = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).getString(getString(R.string.Email), null)
        drawerLoggedUser.text = prefsEmail

        db.collection("users").document(prefsEmail!!).get().addOnSuccessListener {
            val imagen_url = it.get("imagen_foto_url") as String?
            val nombre = it.get("nombre") as String?
            val apellido = it.get("apellido") as String?


            if (nombre.isNullOrBlank() && apellido.isNullOrBlank()) {
                drawerNameUser.isGone
            }else{
                drawerNameUser.text = nombre + " " + apellido
            }

            if (imagen_url != ""){
                storage = dbS.getReference("Users/" + imagen_url)
            }else{
                storage = dbS.getReference("Users/perfil_generico_3.png")
            }

            val localfile = File.createTempFile("tempImage","jpg")
            storage.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                drawerImageUser.setImageBitmap(bitmap)
            }
        }

        val navMenu: Menu = navigationView.menu
        // Acá se podria poner un Alias descriptivo en vez de las URL
        navMenu.findItem(R.id.btnItemInfoAdicional1).setTitle(INTA_URL)
        navMenu.findItem(R.id.btnItemInfoAdicional2).setTitle(BONOS_URL)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)

        when (item.itemId){
            R.id.btnItemHome -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frameLayout, HomeFragment())
                fragmentTransaction.commit()
            }
            R.id.btnItemScan -> {checkearPermisosCamera()}
            R.id.btnInputInfo -> mostrarAlerta("Implementar fragment para Cargar Información","Ingresar Informacion")
            R.id.btnItemParcelas -> mostrarAlerta("Implementar fragment para ver parcelas","Mis Parcelas")
            R.id.btnItemReportes -> mostrarAlerta("Implementar fragment para ver reportes","Reportes")
            R.id.btnItemHuella -> mostrarAlerta("Implementar fragment para ver bonos de carbono","Huella de Carbono")
            R.id.btnItemBuscarSocio -> {
                val intent = Intent(this, RecyclerAsociadosActivity::class.java)
                startActivity(intent)
            }
            R.id.btnItemInfoAdicional1 -> {
                intent.setData(Uri.parse(INTA_URL))
                startActivity(intent)}
            R.id.btnItemInfoAdicional2 -> {
                intent.setData(Uri.parse(BONOS_URL))
                startActivity(intent)}
            R.id.btnItemConfig -> {
                val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
                println(prefs.toString())
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frameLayout, perfilFragment())
                fragmentTransaction.commit()

            }
            R.id.btnItemLogout -> {
                val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
                val proveedor = prefs.getString(getString(R.string.Proveedor), null)
                val prefsClose = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).edit()
                prefsClose.clear()
                prefsClose.apply()

                if(proveedor == UtilsAuth.ProveedorLogin.FACEBOOK.toString()){
                    LoginManager.getInstance().logOut()
                }else{
                    FirebaseAuth.getInstance().signOut()
                }
                onBackPressed()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?){
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkearPermisosCamera(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //El permiso no está estaba aceptado o no tuvo
            solicitarPermisosParaCamara()
        } else {
            //El permiso estaba aceptado, hacer algo.
            Toast.makeText(this, "Ingresando al scan de árboles...", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ScanArbolActivity::class.java)
            startActivity(intent)
        }
    }


    private fun solicitarPermisosParaCamara() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
            mostrarAlerta("Has denegado el uso de la cámara anteriormente. Para acceder al scan deberas dar permisos manualmente.",
                            "Permisos Cámara")
        } else {
            //El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),CAMERA_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El usuario ha aceptado el permiso, no tiene porqué darle de nuevo al botón, podemos lanzar la funcionalidad desde aquí.
                    val intent = Intent(this, ScanArbolActivity::class.java)
                    startActivity(intent)
                } else {
                    //El usuario ha rechazado el permiso, podemos desactivar la funcionalidad o mostrar una vista/diálogo.
                    mostrarAlerta("Has denegado el uso de la camara para scaneo. Para habilitarlo debes cambiarlo manualmente", "Permisos Cámara")
                }
                return
            }
            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
    }

    private fun mostrarAlerta(mensaje: String, titulo: String ){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("¡Entendido!", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}