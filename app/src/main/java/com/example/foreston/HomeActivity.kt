package com.example.foreston

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foreston.databinding.ActivityHomeBinding
import com.facebook.login.Login
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

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

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, HomeFragment())
        fragmentTransaction.commit()

        val headerView = navigationView.getHeaderView(0)
        val drawerLoggedUser = headerView.findViewById<TextView>(R.id.drawerLoggedUser)

        // Setear la imagen del usuario en la la cabecera del Menu
     //   val drawerImageUser = headerView.findViewById<TextView>(R.id.drawerImageUser)
     //   drawerImageUser.background = "Setear imagen o foto del usuario"
        val prefsEmail = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
        drawerLoggedUser.text = prefsEmail.getString("Email", null)
        val navMenu: Menu = navigationView.menu
        // Acá se podria poner un Alias descriptivo en vez de las URL
        navMenu.findItem(R.id.btnItemInfoAdicional1).setTitle(INTA_URL)
        navMenu.findItem(R.id.btnItemInfoAdicional2).setTitle(BONOS_URL)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)

        when (item.itemId){
            R.id.btnItemHome -> mostrarAlerta("Implementar fragment para volver al Home")
            R.id.btnItemScan -> mostrarAlerta("Implementar fragment para escanear árbol")
            R.id.btnInputInfo -> mostrarAlerta("Implementar fragment para Cargar Información")
            R.id.btnItemParcelas -> mostrarAlerta("Implementar fragment para ver parcelas")
            R.id.btnItemReportes -> mostrarAlerta("Implementar fragment para ver reportes")
            R.id.btnItemHuella -> mostrarAlerta("Implementar fragment para ver bonos de carbono")
            R.id.btnItemBuscarSocio -> mostrarAlerta("Implementar fragment para buscar socio")
            R.id.btnItemInfoAdicional1 -> {
                intent.setData(Uri.parse(INTA_URL))
                startActivity(intent)}
            R.id.btnItemInfoAdicional2 -> {
                intent.setData(Uri.parse(BONOS_URL))
                startActivity(intent)}
            R.id.btnItemConfig -> mostrarAlerta("Implementar fragment para cambiar configuración")
            R.id.btnItemLogout -> {
                val prefs = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE)
                val proveedor = prefs.getString("Proveedor", null)
                val prefsClose = getSharedPreferences(getString(R.string.archivo_preferencias), Context.MODE_PRIVATE).edit()
                prefsClose.clear()
                prefsClose.apply()

                if(proveedor == ProveedorLogin.FACEBOOK.toString()){
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

    private fun mostrarAlerta(mensaje: String ){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Implementar!")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Enterado!", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}