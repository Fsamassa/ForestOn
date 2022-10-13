package com.example.foreston

import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.foreston.databinding.ActivityScanArbol2DfijoBinding
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Ray
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import java.text.DecimalFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.Gravity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.foreston.utils.GeneralUtils
import com.google.ar.sceneform.rendering.Color
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class ScanArbol2DfijoActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var binding: ActivityScanArbol2DfijoBinding
    private lateinit var arFragment: ArFragment
    private var diametro = 0.05f
    private var distancia = -1.5f
    private var color = Color(android.graphics.Color.GREEN)
    private var node : Node = Node()
    private lateinit var renderable : ModelRenderable
    private lateinit var file: File
    private lateinit var bitmapGlobal: Bitmap
    private lateinit var uriGlobal: Uri

    private val TEXTURA_TRANSLUCIDA = 1
    private val TEXTURA_OPACO = 2
    private val TEXTURA_EUCALIPTO = 3
    private val TEXTURA_TRANSPARENTE = 4
    private var texturaElegida = 1
    private val dbS = FirebaseStorage.getInstance()
    private lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanArbol2DfijoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvDistancia.visibility = View.GONE

        arFragment = supportFragmentManager.findFragmentById(R.id.fragmentFijo) as ArFragment
        arFragment.arSceneView.planeRenderer.isEnabled = false

        seleccionDiametros()

        binding.btnTranslucido.setOnClickListener {
            texturaElegida = TEXTURA_TRANSLUCIDA
        }
        binding.btnOpaco.setOnClickListener {
            texturaElegida = TEXTURA_OPACO
        }
        binding.btnEucalipto.setOnClickListener {
            texturaElegida = TEXTURA_EUCALIPTO
        }
        binding.btnTransparente.setOnClickListener {
            texturaElegida = TEXTURA_TRANSPARENTE
        }

        binding.btnDistancia.setOnClickListener {
            binding.vHorizontal.setBackgroundColor(android.graphics.Color.RED)
            binding.vVertical.setBackgroundColor(android.graphics.Color.RED)
            binding.tvDistancia.visibility = View.VISIBLE
            arFragment.arSceneView.planeRenderer.isEnabled = true

            if (arFragment.arSceneView.planeRenderer.isEnabled) {
                arFragment.setOnTapArPlaneListener{hitResult, _ , _ ->

                    val df = DecimalFormat("#.##")
                    distancia = hitResult.distance
                    val distanciaEnDecimal = df.format(distancia)

                    binding.vHorizontal.setBackgroundColor(resources.getColor(R.color.botones_inicio))
                    binding.vVertical.setBackgroundColor(resources.getColor(R.color.botones_inicio))

                    binding.tvDistancia.text = "- " + distanciaEnDecimal.toString() + " Metros -"

                    distancia *= -1

                    binding.grupoDistancia.selectButtonWithAnimation(binding.btnDistancia)
                    arFragment.arSceneView.planeRenderer.isEnabled = false
                }
            }

        }
        binding.btnCamara.setOnClickListener {
            arFragment.arSceneView.planeRenderer.isEnabled = false
            tomarCapturaPantalla()

            GeneralUtils.mostrarAlertaDecision(
                this,
                "Medición tomada exitosamente! ¿Ver información detallada?",
                null, "Seguir escaneando",
                positiveAction = {

                    val diametroEnCM = diametro * 100
                    val df = DecimalFormat("##.##")
                    val diametroSinDecimal = df.format(diametroEnCM)

                    val intent = Intent(this, InfoScaneadaActivity::class.java)
                    intent.putExtra("diametro", diametroSinDecimal.toString())
                    intent.putExtra("imagen", uriGlobal)
                    startActivity(intent)
                },
                negativeAction = null)
            arFragment.arSceneView.planeRenderer.isEnabled = true

        }
        binding.btnCompartirImagen.setOnClickListener {
            val view = arFragment.getArSceneView()
            GeneralUtils.usePixelCopy(view) { bitmap: Bitmap? ->
                val now = Date()
                DateFormat.format("yyyy-mm-dd_hh:mm:ss", now)
                val path = getExternalFilesDir(null)!!.absolutePath + "/"+now+".jpg"
                val imageFile = File(path)
                val outputStream = FileOutputStream(imageFile)
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                val URI = FileProvider.getUriForFile(this,"com.e.capturescreenshot.android.fileprovider", imageFile)
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT,
                    "|- Scaner de Medición Forestal -|"+ "\n" +
                            "---------------------------------" + "\n" +
                            "Captura tomada con ForestOn APP")
                intent.putExtra(Intent.EXTRA_STREAM,URI)
                intent.type = "text/plain"
                startActivity(intent)
            }
        }

    }

    private fun seleccionDiametros() {
        val diametrosString = resources.getStringArray(R.array.diametros_validos)

        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_item, diametrosString)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        with(binding.autoCompleteTextView){
            onItemClickListener = this@ScanArbol2DfijoActivity
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        diametro = (position.toFloat() + 1) / 100

        arFragment.arSceneView.scene.removeChild(node)

        when (texturaElegida) {
            TEXTURA_TRANSLUCIDA -> {
                when(position){
                    in 0..9 -> {
                        color = Color(0f,1f,0f,0f)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_verde))
                    }
                    in 10..19 -> {
                        color = Color(1f,0f,1f,0f)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_magenta))
                    }
                    in 20..29 -> {
                        color = Color(0f,0f,1f,0f)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_azul))
                    }
                    in 30..39 -> {
                        color =  Color(251f,227f,8f,0f)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_amarillo))
                    }
                    in 40..49 -> {
                        color = Color(1f,0f,0f,0f)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_rojo))
                    }
                }
                generarCilindroTransparenteMedicion()
            }
            TEXTURA_OPACO -> {
                when(position){
                    in 0..9 -> {
                        color = Color(android.graphics.Color.GREEN)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_verde))
                    }
                    in 10..19 -> {
                        color = Color(android.graphics.Color.MAGENTA)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_magenta))
                    }
                    in 20..29 -> {
                        color = Color(android.graphics.Color.BLUE)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_azul))
                    }
                    in 30..39 -> {
                        color = Color(android.graphics.Color.YELLOW)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_amarillo))
                    }
                    in 40..49 -> {
                        color = Color(android.graphics.Color.RED)
                        binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.seleccion_rojo))
                    }
                }
                generarCilindroOpacoMedicion()
            }
            TEXTURA_EUCALIPTO -> {
                binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.contor_general))
                generarCilindroTexturaMedicion()
            }
            TEXTURA_TRANSPARENTE -> {
                color = Color(0f,0f,0f,0f)
                binding.tilDropDown.endIconDrawable!!.setTint(resources.getColor(R.color.white))
                generarCilindroTransparenteMedicion()
            }
        }

    }


    private fun generarCilindroTexturaMedicion(){
        Texture.builder().setSource(this, R.drawable.textura_eucalipto).build().thenAccept {
            texture ->
                MaterialFactory.makeOpaqueWithTexture(this, texture).thenAccept { material ->
                    renderable = ShapeFactory.makeCylinder( diametro, 2f , Vector3(0f, -0.35f, distancia * 0.9f), material)
                    node.setParent(arFragment.arSceneView.scene)
                    node.renderable = renderable
                    renderable.isShadowCaster = false

                    arFragment.arSceneView.scene.addOnUpdateListener {
                            frameTime ->
                        val camera : Camera = arFragment.arSceneView.scene.camera
                        val ray : Ray = camera.screenPointToRay(1080 / 2f, 1920 / 2f)

                        val posicion : Vector3 = ray.getPoint(1f)

                        node.localPosition = posicion }
                }
        }
    }
    private fun generarCilindroTransparenteMedicion(){
        MaterialFactory.makeTransparentWithColor(this, color).thenAccept{ material ->
            renderable = ShapeFactory.makeCylinder( diametro, 2f , Vector3(0f, -0.35f, distancia * 0.9f), material)
            renderable.isShadowCaster = false

            node.setParent(arFragment.arSceneView.scene)
            node.renderable = renderable

            arFragment.arSceneView.scene.addOnUpdateListener {
                    frameTime ->
                val camera : Camera = arFragment.arSceneView.scene.camera
                val ray : Ray = camera.screenPointToRay(1080 / 2f, 1920 / 2f)

                val posicion : Vector3 = ray.getPoint(1f)

                node.localPosition = posicion }
        }
    }
    private fun generarCilindroOpacoMedicion(){
        MaterialFactory.makeOpaqueWithColor(this, color).thenAccept{ material ->
            renderable = ShapeFactory.makeCylinder( diametro, 2f , Vector3(0f, -0.35f, distancia * 0.9f), material)
            renderable.isShadowCaster = false

            node.setParent(arFragment.arSceneView.scene)
            node.renderable = renderable

            arFragment.arSceneView.scene.addOnUpdateListener {
                    frameTime ->
                val camera : Camera = arFragment.arSceneView.scene.camera
                val ray : Ray = camera.screenPointToRay(1080 / 2f, 1920 / 2f)

                val posicion : Vector3 = ray.getPoint(1f)

                node.localPosition = posicion }
        }
    }

    private fun guardarRepositorioRemoto(){
        val view = arFragment.getArSceneView()
        GeneralUtils.usePixelCopy(view) { bitmap: Bitmap? ->
            val now = Date()
            DateFormat.format("yyyy-mm-dd_hh:mm:ss", now)
            val path = getExternalFilesDir(null)!!.absolutePath + "/"+now+".jpg"
            val imageFile = File(path)
            val outputStream = FileOutputStream(imageFile)

            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val URI = FileProvider.getUriForFile(this,"com.e.capturescreenshot.android.fileprovider", imageFile)

            uriGlobal = URI
            // Ver de borrar el archivo generado en "path" TODO

            storageReference = dbS.getReference("Users/ImagenDeCapturaPantalla.JPEG")
            storageReference.putFile(URI)
                .addOnSuccessListener {
                    val mensajeToast = Toast.makeText(
                        this,
                        "Imagen cargada en la Base de Datos de Foreston...",
                        Toast.LENGTH_SHORT)
                    mensajeToast.setGravity(Gravity.TOP,0 ,0)
                    mensajeToast.show() }
                .addOnFailureListener {
                    val mensajeToast = Toast.makeText(
                        this,
                        "Error al querer cagar imagen en la Base de Datos de Foreston...",
                        Toast.LENGTH_SHORT)
                    mensajeToast.setGravity(Gravity.TOP,0 ,0)
                    mensajeToast.show()
                }
        }
    }

    private fun tomarCapturaPantalla() {
        val view = arFragment.getArSceneView()
        GeneralUtils.usePixelCopy(view) { bitmap: Bitmap? ->
            bitmapGlobal = bitmap!!
            guardarLocalmente()
        }
        val mensajeToast = Toast.makeText(this, "Imagen guardada exitosamente en Galeria...", Toast.LENGTH_SHORT)
        mensajeToast.setGravity(Gravity.TOP,0 ,0)
        mensajeToast.show()
    }
    private fun guardarLocalmente()  {
        val content = createContent()

        val uri = save(content)

        uriGlobal = uri
        clearContents(content,uri)
    }
    private fun createContent(): ContentValues {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_",".jpg",dir)
        val fileName = file.name
        val fileType = "image/jpg"
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,fileName)
            put(MediaStore.Files.FileColumns.MIME_TYPE,fileType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING,1)
            }
        }
    }
    private fun save(content: ContentValues): Uri {
        var outputStream2 : OutputStream?
        var uri : Uri?
        application.contentResolver.also { resolver ->
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,content)
            outputStream2 = resolver.openOutputStream(uri!!)
        }
        outputStream2.use { outPut ->
            bitmapGlobal.compress(Bitmap.CompressFormat.JPEG, 100, outPut)

        }
        return uri!!
    }
    private fun clearContents(content: ContentValues, uri: Uri) {
        content.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            content.put(MediaStore.MediaColumns.IS_PENDING,0)
        }
        contentResolver.update(uri,content,null,null)
    }
}
/* Lo dejo por las dudas pero se implementa el drowdown menu de diametros
        binding.btn5CM.setOnClickListener {
            diametro = 0.05f
            arFragment.arSceneView.scene.removeChild(node)

            when (texturaElegida) {
                TEXTURA_TRANSLUCIDA -> {
                    color = Color(0f,1f,0f,0f)
                    generarCilindroTransparenteMedicion()
                }
                TEXTURA_OPACO -> {
                    color = Color(android.graphics.Color.GREEN)
                    generarCilindroOpacoMedicion()
                }
                TEXTURA_EUCALIPTO -> {
                    generarCilindroTexturaMedicion()
                }
                TEXTURA_TRANSPARENTE -> {
                    color = Color(0f,0f,0f,0f)
                    generarCilindroTransparenteMedicion()
                }
            }

        }
        binding.btn10CM.setOnClickListener {
            diametro = 0.10f
            arFragment.arSceneView.scene.removeChild(node)

            when (texturaElegida) {
                TEXTURA_TRANSLUCIDA -> {
                    color = Color(1f,0f,1f,0f)
                    generarCilindroTransparenteMedicion()
                }
                TEXTURA_OPACO -> {
                    color = Color(android.graphics.Color.MAGENTA)
                    generarCilindroOpacoMedicion()
                }
                TEXTURA_EUCALIPTO -> {
                    generarCilindroTexturaMedicion()
                }
                TEXTURA_TRANSPARENTE -> {
                    color = Color(0f,0f,0f,0f)
                    generarCilindroTransparenteMedicion()
                }
            }

        }
        binding.btn15CM.setOnClickListener {
            diametro = 0.15f
            arFragment.arSceneView.scene.removeChild(node)

            when (texturaElegida) {
                TEXTURA_TRANSLUCIDA -> {
                    color = Color(0f,0f,1f,0f)
                    generarCilindroTransparenteMedicion()
                }
                TEXTURA_OPACO -> {
                    color = Color(android.graphics.Color.BLUE)
                    generarCilindroOpacoMedicion()
                }
                TEXTURA_EUCALIPTO -> {
                    generarCilindroTexturaMedicion()
                }
                TEXTURA_TRANSPARENTE -> {
                    color = Color(0f,0f,0f,0f)
                    generarCilindroTransparenteMedicion()
                }
            }
        }
        binding.btn20CM.setOnClickListener {
            diametro = 0.20f
            arFragment.arSceneView.scene.removeChild(node)

            when (texturaElegida) {
                TEXTURA_TRANSLUCIDA -> {
                    color = Color(251f,227f,8f,0f)
                    generarCilindroTransparenteMedicion()
                }
                TEXTURA_OPACO -> {
                    color = Color(android.graphics.Color.YELLOW)
                    generarCilindroOpacoMedicion()
                }
                TEXTURA_EUCALIPTO -> {
                    generarCilindroTexturaMedicion()
                }
                TEXTURA_TRANSPARENTE -> {
                    color = Color(0f,0f,0f,0f)
                    generarCilindroTransparenteMedicion()
                }
            }
        }
        binding.btn30CM.setOnClickListener {
            diametro = 0.30f
            arFragment.arSceneView.scene.removeChild(node)

            when (texturaElegida) {
                TEXTURA_TRANSLUCIDA -> {
                    color = Color(1f,0f,0f,0f)
                    generarCilindroTransparenteMedicion()
                }
                TEXTURA_OPACO -> {
                    color = Color(android.graphics.Color.RED)
                    generarCilindroOpacoMedicion()
                }
                TEXTURA_EUCALIPTO -> {
                    generarCilindroTexturaMedicion()
                }
                TEXTURA_TRANSPARENTE -> {
                    color = Color(0f,0f,0f,0f)
                    generarCilindroTransparenteMedicion()
                }
            }
        }
*/

/* Información adicional sobre ideas
 En la coordenada Z me queda fija la distancia, ya sé que va a estar cierta cantidad de metros,
 o mejor dicho, que si se para a esa cantidad de metros el diametro es el que calce según la imagen creada
 frame.hittest everytime I clicked the button. Now I can use hitresult.createAnchor and get on with it.
 con un boton centrado en la camara "disparar" y marcar 2 puntos que seteen el diametro del vector que será usado para
 armar el cilindro -> val anchor = hitResult.createAnchor()
   hitResult.getDistance()
      renderable.isShadowReceiver = false  -> Para que una sombra reflejada por otro objeto no se le vea encima.
      renderable.isShadowCaster = false    -> Para que no proyecte sombra
      arFragment.arSceneView.planeRenderer.isEnabled = false -> para que no se vean los puntos

   */