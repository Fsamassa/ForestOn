package com.example.foreston

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foreston.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.btnItemHome -> mostrarAlerta("Implementar fragment para volver al Home")
            R.id.btnItemScan -> mostrarAlerta("Implementar fragment para escanear árbol")
            R.id.btnInputInfo -> mostrarAlerta("Implementar fragment para Cargar Información")
            R.id.btnItemParcelas -> mostrarAlerta("Implementar fragment para ver parcelas")
            R.id.btnItemReportes -> mostrarAlerta("Implementar fragment para ver reportes")
            R.id.btnItemHuella -> mostrarAlerta("Implementar fragment para ver bonos de carbono")
            R.id.btnItemBuscarSocio -> mostrarAlerta("Implementar fragment para buscar socio")
            R.id.btnItemInfoAdicional1 -> mostrarAlerta("Implementar fragment para ir a un link1")
            R.id.btnItemInfoAdicional2 -> mostrarAlerta("Implementar fragment para ir a un link2")
            R.id.btnItemConfig -> mostrarAlerta("Implementar fragment para cambiar configuración")
            R.id.btnItemLogout -> mostrarAlerta("Implementar fragment para Cerrar sesión")
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