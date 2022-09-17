package com.example.foreston

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.foreston.databinding.ActivityScanArbolBinding
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class ScanArbolActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanArbolBinding
    private lateinit var arFragment: ArFragment
    private lateinit var selectedObject: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanArbolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment_view) as ArFragment

        setModelPath("rocket.sfb")

        arFragment.setOnTapArPlaneListener{hitResult, plane, _ ->
            if(plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING){
                return@setOnTapArPlaneListener
            }
            val anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor, selectedObject)
        }

        binding.smallTable.setOnClickListener {
            setModelPath("model.sfb")
        }
        binding.bigLamp.setOnClickListener{
            setModelPath("LampPost.sfb")
        }
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor, modelUri: Uri){
        val modelRenderable = ModelRenderable.builder()
            .setSource((fragment.requireContext()), modelUri)
            .build()

        modelRenderable.thenAccept { renderableObject ->
            addNodeToScene(fragment,anchor, renderableObject)
        }

        modelRenderable.exceptionally {
            val toast = Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT)
            toast.show()
            println("** no renderiza** ")
            null
        }
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderableObject: Renderable){
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderableObject
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }

    private fun setModelPath(modelFileName: String){
        selectedObject = Uri.parse(modelFileName)
        val toast = Toast.makeText(applicationContext, modelFileName, Toast.LENGTH_SHORT)
        toast.show()
    }

}