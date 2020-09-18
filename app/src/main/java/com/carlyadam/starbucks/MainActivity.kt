package com.carlyadam.starbucks

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carlyadam.starbucks.databinding.ActivityMainBinding
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions


class MainActivity : AppCompatActivity() {

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
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionLabelDetectorOptions.Builder()
            .setConfidenceThreshold(0.8f)
            .build()
        val detector = FirebaseVision.getInstance().getVisionLabelDetector(options)

        //2
        detector.detectInImage(image)
            //3
            .addOnSuccessListener {
                binding.progressBar.visibility = View.INVISIBLE

                val value = it.map { it.label.toString() }

                Toast.makeText(this, value.toString(), Toast.LENGTH_SHORT).show()

                binding.imageViewCapture.setImageBitmap(bitmap)


            }//4
            .addOnFailureListener {
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()

            }
    }
}