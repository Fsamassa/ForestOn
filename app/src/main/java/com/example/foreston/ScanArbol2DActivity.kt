package com.example.foreston

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.foreston.databinding.ActivityScanArbol2DactivityBinding
import com.example.foreston.utils.GeneralUtils
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class ScanArbol2DActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanArbol2DactivityBinding
    private lateinit var arFragment: ArFragment
    private var diametro = 0.05f
    private var altura = 2f
    private lateinit var renderable : ModelRenderable
    private val dbS = FirebaseStorage.getInstance()
    private lateinit var storageReference: StorageReference
    private lateinit var file: File
    private lateinit var bitmapGlobal: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanArbol2DactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor = hitResult.createAnchor()
                Texture.builder().setSource(this, R.drawable.textura_eucalipto).build().thenAccept {
                        texture ->
                    MaterialFactory.makeOpaqueWithTexture(this, texture).thenAccept { material ->
                        renderable = ShapeFactory.makeCylinder( diametro, altura , Vector3(0f, 0f, 0f), material)
                        val anchorNode = AnchorNode(anchor)
                        val transformableNode = TransformableNode(arFragment.transformationSystem)
                        transformableNode.renderable = renderable
                        transformableNode.setParent(anchorNode)
                        arFragment.arSceneView.scene.addChild(anchorNode)
                        transformableNode.select()
                    }
                }
        }

        binding.btn5CM.setOnClickListener  { diametro = 0.05f}
        binding.btn10CM.setOnClickListener { diametro = 0.10f}
        binding.btn15CM.setOnClickListener { diametro = 0.15f}
        binding.btn20CM.setOnClickListener { diametro = 0.20f}
        binding.btn25CM.setOnClickListener { diametro = 0.25f}

        binding.btn1MTR.setOnClickListener { altura = 1f}
        binding.btn2MTR.setOnClickListener { altura = 2f}
        binding.btn3MTR.setOnClickListener { altura = 3f}
        binding.btn4MTR.setOnClickListener { altura = 4f}
        binding.btn5MTR.setOnClickListener { altura = 5f}

        binding.btnCamara.setOnClickListener {
            arFragment.arSceneView.planeRenderer.isEnabled = false  //  <- Ver esto para que no salgan en la foto los puntos blancos
            tomarCapturaPantalla()
       //   guardarRepositorioRemoto()  Aca iria una validación de seleccion entre galeria/repo TODO
            arFragment.arSceneView.planeRenderer.isEnabled = true   //  <- Ver esto para que no salgan en la foto los puntos blancos
        }
        binding.btnCompartirImagen.setOnClickListener{
            // Con esto lo que hago es generar un archivo en una carpeta interna de android que me permite
            // recuperarlo si quiero mostrarlo en una imageView por ejemplo, y que mantenga toda la resolución
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
                intent.putExtra(Intent.EXTRA_TEXT, "Titulo envio"+ "\n"+ "Descripción mensaje")
                intent.putExtra(Intent.EXTRA_STREAM,URI)
                intent.type = "text/plain"
                startActivity(intent)
            }
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

            // Ver de borrar el archivo generado en "path" TODO

            storageReference = dbS.getReference("Users/ImagenDeCapturaPantalla.JPEG")
            storageReference.putFile(URI)
                .addOnSuccessListener {
                    val mensajeToast = Toast.makeText(
                    this,
                    "Imagen cargada en la Base de Datos de Foreston...",
                    Toast.LENGTH_SHORT)
                    mensajeToast.setGravity(Gravity.CENTER,0 ,0)
                    mensajeToast.show() }
                .addOnFailureListener {
                    val mensajeToast = Toast.makeText(
                    this,
                    "Error al querer cagar imagen en la Base de Datos de Foreston...",
                    Toast.LENGTH_SHORT)
                    mensajeToast.setGravity(Gravity.CENTER,0 ,0)
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
        mensajeToast.setGravity(Gravity.CENTER,0 ,0)
        mensajeToast.show()
    }
    private fun guardarLocalmente() {
        val content = createContent()

        val uri = save(content)

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
                put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES)
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