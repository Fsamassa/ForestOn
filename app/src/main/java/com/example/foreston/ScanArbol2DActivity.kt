package com.example.foreston

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foreston.databinding.ActivityScanArbol2DactivityBinding
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class ScanArbol2DActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanArbol2DactivityBinding
    private lateinit var arFragment: ArFragment
    private var diametro = 0.05f
    private var altura = 2f
    private lateinit var renderable : ModelRenderable

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
    }
}