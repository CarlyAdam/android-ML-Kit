package com.carlyadam.starbucks

//import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.carlyadam.starbucks.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions


class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.INVISIBLE

        binding.buttonCamera.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() = runWithPermissions(Manifest.permission.CAMERA) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 11)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11) {
            val photo = data!!.extras!!.get("data") as (Bitmap)
            checkPic(photo)
        }
    }

    private fun checkPic(bitmap: Bitmap) {
        binding.progressBar.visibility = View.VISIBLE
        val localModel = AutoMLImageLabelerLocalModel.Builder()
            .setAssetFilePath("manifest.json")
            .build()

        val autoMLImageLabelerOptions = AutoMLImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0F)  // Evaluate your model in the Firebase console
            // to determine an appropriate value.
            .build()

        val labeler = ImageLabeling.getClient(autoMLImageLabelerOptions)

        val image = InputImage.fromBitmap(bitmap, 0)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }


    }
}