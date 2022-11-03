package com.example.foreston.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import com.example.foreston.R
import com.google.ar.sceneform.ArSceneView
import java.text.DecimalFormat
import kotlin.math.round

object GeneralUtils {

    fun mostrarAlerta(context: Context, message: String, buttonText: String? = null, buttonAction: (() -> Unit)? = null,) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.app_name))
        builder.setMessage(message)
        builder.setPositiveButton(buttonText ?: context.getString(R.string.Aceptar)) { dialog, _ ->
            if (buttonAction != null) {
                buttonAction()
            }
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }
    fun mostrarAlertaDecision(
        context: Context,
        message: String,
        positiveText: String? = null,
        negativeText: String? = null,
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null,
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.app_name))
        builder.setMessage(message)
        builder.setPositiveButton(positiveText ?: context.getString(R.string.SI)) { dialog, _ ->
            if (positiveAction != null) {
                positiveAction()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(negativeText ?: context.getString(R.string.NO)) { dialog, _ ->
            if (negativeAction != null) {
                negativeAction()
            }
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }
    /**
     * Pixel copy to copy SurfaceView/VideoView into BitMap
     */

    fun usePixelCopy(imagenView: ArSceneView, callback: (Bitmap?) -> Unit) {
        val bitmap: Bitmap = Bitmap.createBitmap(
            imagenView.width,
            imagenView.height,
            Bitmap.Config.ARGB_8888
        )
        try {
            // Create a handler thread to offload the processing of the image.
            val handlerThread = HandlerThread("PixelCopier")
            handlerThread.start()

            PixelCopy.request(
                imagenView, bitmap,
                PixelCopy.OnPixelCopyFinishedListener { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        callback(bitmap)
                    }
                    handlerThread.quitSafely();
                },
                Handler(handlerThread.looper)
            )
        } catch (e: IllegalArgumentException) {
            callback(null)
            // PixelCopy may throw IllegalArgumentException, make sure to handle it
            e.printStackTrace()
        }
    }

    fun formatearNumerosGrandes(valor : Double): String? {
        val df = DecimalFormat("#,##0.00")
        return df.format(valor)
    }
    fun formatearDecimales(valor : Double): String? {
        val df = DecimalFormat("#0.00")
        return df.format(valor)
    }
    fun quitarDecimales(valor : Double): String? {
        val df = DecimalFormat("##")
        return df.format(valor)
    }
    fun pasarEdadanios(meses : String): String? {
        var edadAnios = meses.toDouble() / 12
        val mesesRestantes = (edadAnios % 1) * 12
        edadAnios = edadAnios - (edadAnios % 1)

        return "${edadAnios.toInt()} años y ${(round(mesesRestantes)).toInt()} meses"
    }

}


/*
    fun compartirCapturaDePantalla(view: View, context: Context)   {

      // var imagenFrame = arFragment.arSceneView.arFrame!!.acquireCameraImage()

       val now = Date()
       DateFormat.format("yyyy-mm-dd_hh:mm:ss", now)

       val path = getExternalFilesDir(null)!!.absolutePath + "/"+now+".jpg"
       var bitmamp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
       var canvas = Canvas(bitmamp)

       val vista = View
       vista.draw(canvas)
       val imageFile = File(path)
       val outputStream = FileOutputStream(imageFile)
       bitmamp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
       outputStream.flush()
       outputStream.close()

       val URI = FileProvider.getUriForFile(context,"com.e.capturescreenshot.android.fileprovider", imageFile)
       val intent = Intent()
       intent.action = Intent.ACTION_SEND
       intent.putExtra(Intent.EXTRA_TEXT, "Titulo envio"+ "\n"+ "Descripción mensaje")
       intent.putExtra(Intent.EXTRA_STREAM,URI)
       intent.type = "text/plain"
       startActivity(intent)

    }
*/