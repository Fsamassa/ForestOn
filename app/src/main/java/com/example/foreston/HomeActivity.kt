package com.example.foreston

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foreston.componentesTabLayout.HomeFragment
import com.example.foreston.databinding.ActivityHomeBinding
import com.example.foreston.recyclerAsociados.RecyclerAsociadosActivity
import com.example.foreston.recyclerParcelas.RecyclerParcelasActivity
import com.example.foreston.utils.GeneralUtils
import com.example.foreston.utils.UtilsAuth
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
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
    private val STORAGE_REQUEST_CODE = 1
    private val ESCANEO_ARBOL = 1
    private val SIMULADOR_ARBOL = 2
    private var ingresoSeleccionado = 1

    // urls test
  //  private var INTA_URL: String = "https://inta.gob.ar/sites/default/files/inta_concordia_planilla_de_precios_forestales_junio_2022.pdf"
    //   private var MANUAL_URL: String = "https://inta.gob.ar/sites/default/files/script-tmp-manual_para_productores_de_eucaliptos_de_la_mesopotam.pdf"
    private var INTA_URL: String = "https://sites.google.com/view/preciosforestales-inta/inicio"
    private var BONOS_URL: String = "http://www.ceads.org.ar/mercado-de-bonos-de-carbono-voluntario-vs-regulado/"
    private var MANUAL_URL: String = "https://sites.google.com/view/manual-productores-eucalipto/inicio"

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
        navMenu.findItem(R.id.btnItemInfoAdicional1).setTitle("Precios por Industria")
        navMenu.findItem(R.id.btnItemInfoAdicional2).setTitle("¿Bonos de Carbono?")
        navMenu.findItem(R.id.btnItemInfoAdicional3).setTitle("Manual para el Productor")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)

        when (item.itemId){
            R.id.btnItemHome -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frameLayout, HomeFragment())
                fragmentTransaction.commit()
            }
            R.id.btnItemScan -> {
                ingresoSeleccionado = 1
                checkearPermisos(ingresoSeleccionado)}
            R.id.btnItemSimularArbol -> {
                ingresoSeleccionado = 2
                checkearPermisos(ingresoSeleccionado)}
            R.id.btnInputInfo -> {
                val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
                val intento1= Intent(this,InformacionActivity::class.java)
                startActivity(intento1)
            }
            R.id.btnItemParcelas -> {
                // TODO Poner validación para cuando no tiene parcelas para que no entre
                /*
                val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
                val email = prefs.getString("Email", null)

                GeneralUtils.mostrarAlerta(
                        this,
                        "Para ver tu Forestación debes ingresar alguna alguna parcela desde el" +
                                " menú lateral \"Ingresar Información\" o utilizando el escaner de árboles"
                )

                 */

                val intent= Intent(this, RecyclerParcelasActivity::class.java)
                startActivity(intent)

            }
            R.id.btnItemReportes -> {
                val intent = Intent(this, MisReportesActivity::class.java)
                startActivity(intent)}
            R.id.btnItemBono -> {
                val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
                val email = prefs.getString("Email", null)

                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val fragmentHuella = HuellaFragment()
                fragmentHuella.arguments = Bundle().apply {
                    putString("EMAIL_USUARIO", email)
                }
                fragmentTransaction.replace(R.id.frameLayout, fragmentHuella)
                fragmentTransaction.commit()
            }
            R.id.btnItemBuscarSocio -> {
                val intent = Intent(this, RecyclerAsociadosActivity::class.java)
                startActivity(intent)}
            R.id.btnItemInfoAdicional1 -> {
                intent.setData(Uri.parse(INTA_URL))
                startActivity(intent)}
            R.id.btnItemInfoAdicional2 -> {
                intent.setData(Uri.parse(BONOS_URL))
                startActivity(intent)}
            R.id.btnItemInfoAdicional3 -> {
                intent.setData(Uri.parse(MANUAL_URL))
                startActivity(intent)}
            R.id.btncertificaciones ->{
                val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
                var email = prefs.getString("Email", null)
                val intent= Intent(this, CertificacionesActivity::class.java)
                intent.putExtra("email",email)
                startActivity(intent)
            }
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
                val intent = Intent(this, IngresoActivity::class.java)
                startActivity(intent)
                finish()
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
    private fun checkearPermisos(ingresoElegido: Int){
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            //El permiso no está estaba aceptado o no tuvo
            solicitarPermisosCamara()

        } else {
            //El permiso estaba aceptado, hacer algo.
            ingresoASimuladores(ingresoElegido)
        }
    }
    private fun solicitarPermisosCamara() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
            mostrarAlerta("Has denegado el uso de la cámara anteriormente. Para acceder al scan deberás dar permisos manualmente.",
                            "Permisos Cámara")
        } else {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),CAMERA_REQUEST_CODE)
        }
    }
    private fun solicitarPermisosStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
            mostrarAlerta("Has denegado el uso del Almacenamiento. Para acceder al scan deberás dar permisos manualmente.",
                "Permisos Almacenamiento")
        } else {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),STORAGE_REQUEST_CODE)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El usuario ha aceptado el permiso, no tiene porqué darle de nuevo al botón, podemos lanzar la funcionalidad desde aquí.
                    solicitarPermisosStorage()
                } else {
                    //El usuario ha rechazado el permiso, podemos desactivar la funcionalidad o mostrar una vista/diálogo.
                    mostrarAlerta("Has denegado el uso de la camara para scaneo. Para habilitarlo deberás cambiarlo manualmente desde configuración general de tu dispositivo", "Permisos Cámara")
                }
                return
            }
            STORAGE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    ingresoASimuladores(ingresoSeleccionado)
                }else{
                    mostrarAlerta("Has denegado el uso de almacenamiento. Para habilitarlo deberás cambiarlo manualmente desde configuración general de tu dispositivo", "Permisos de Almacenamiento")
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
    private fun ingresoASimuladores(ingresoElegido : Int){
        when(ingresoElegido){
            ESCANEO_ARBOL -> {
                val mensajeToast = Toast.makeText(this, "Ingresando al scan de árboles...", Toast.LENGTH_SHORT)
                mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                mensajeToast.show()
                val intent = Intent(this, ScanArbol2DfijoActivity::class.java)
                startActivity(intent)
            }
            SIMULADOR_ARBOL -> {
                val mensajeToast = Toast.makeText(this, "Ingresando al simulador de árboles...", Toast.LENGTH_SHORT)
                mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                mensajeToast.show()
                val intent = Intent(this, ScanArbol2DActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {

        GeneralUtils.mostrarAlertaDecision(
            this,
            "¿ Deseas salir de ForestOn ?",
            "Salir", null,
            positiveAction = {
                System.exit(0)
            },
            negativeAction = null
        )
    }
}