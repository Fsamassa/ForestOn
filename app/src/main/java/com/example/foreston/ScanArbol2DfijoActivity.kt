package com.example.foreston

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.example.foreston.databinding.ActivityScanArbol2DfijoBinding
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Ray
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import java.text.DecimalFormat

class ScanArbol2DfijoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanArbol2DfijoBinding
    private lateinit var arFragment: ArFragment
    private var diametro = 0.05f
    private var distancia = -1.5f
    private var color = Color(android.graphics.Color.GREEN)
    private var node : Node = Node()
    private lateinit var renderable : ModelRenderable

    private val TEXTURA_TRANSLUCIDA = 1
    private val TEXTURA_OPACO = 2
    private val TEXTURA_EUCALIPTO = 3
    private val TEXTURA_TRANSPARENTE = 4
    private var texturaElegida = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanArbol2DfijoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDistancia.visibility = View.GONE
        arFragment = supportFragmentManager.findFragmentById(R.id.fragmentFijo) as ArFragment

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
        binding.btn50CM.setOnClickListener {
            diametro = 0.35f
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
        binding.btnDistancia.setOnClickListener {

            binding.vHorizontal.setBackgroundColor(android.graphics.Color.RED)
            binding.vVertical.setBackgroundColor(android.graphics.Color.RED)
            binding.btnDistancia.visibility = View.GONE
            binding.tvDistancia.visibility = View.VISIBLE

            arFragment.setOnTapArPlaneListener{hitResult, _ , _ ->

                val df = DecimalFormat("#.##")
                distancia = hitResult.distance
                val distanciaEnDecimal = df.format(distancia)

                binding.vHorizontal.setBackgroundColor(resources.getColor(R.color.botones_inicio))
                binding.vVertical.setBackgroundColor(resources.getColor(R.color.botones_inicio))
                binding.btnDistancia.visibility = View.VISIBLE
                binding.tvDistancia.text = "- " + distanciaEnDecimal.toString() + " Metros -"

                distancia *= -1

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

}




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