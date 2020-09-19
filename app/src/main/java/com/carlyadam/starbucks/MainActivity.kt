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


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.INVISIBLE
        binding.textViewResult.visibility = View.INVISIBLE
        binding.imageViewCapture.visibility = View.INVISIBLE

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
            binding.imageViewCapture.setImageBitmap(photo)
            binding.imageViewCapture.visibility = View.VISIBLE
            binding.textViewResult.visibility = View.VISIBLE
            checkPic(photo)
        }
    }

    private fun checkPic(bitmap: Bitmap) {
        binding.textViewResult.text =""
        binding.progressBar.visibility = View.VISIBLE
        val localModel = AutoMLImageLabelerLocalModel.Builder()
            .setAssetFilePath("manifest.json")
            .build()

        val autoMLImageLabelerOptions = AutoMLImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0F)
            .build()

        val labeler = ImageLabeling.getClient(autoMLImageLabelerOptions)

        val image = InputImage.fromBitmap(bitmap, 0)

        labeler.process(image)
            .addOnSuccessListener { labels ->

                binding.progressBar.visibility = View.INVISIBLE
                labels.forEach {
                    val text = it.text
                    val confidence = it.confidence
                    binding.textViewResult.text =
                        " ${binding.textViewResult.text} $text $confidence \n"
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.INVISIBLE
                e.printStackTrace()
            }


    }
}