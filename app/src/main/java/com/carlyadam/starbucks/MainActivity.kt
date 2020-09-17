package com.carlyadam.starbucks

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.carlyadam.starbucks.databinding.ActivityMainBinding
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewMessage.visibility = View.INVISIBLE
        binding.imageViewCheck.visibility = View.INVISIBLE

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
        val photo = data!!.extras!!.get("data") as (Bitmap)
        checkPic(photo)
    }

    private fun checkPic(photo: Bitmap) {

        binding.imageViewCapture.setImageBitmap(photo)

        binding.textViewMessage.visibility = View.VISIBLE
        binding.textViewMessage.text = getString(R.string.congrats)

        binding.imageViewCheck.visibility = View.VISIBLE
        binding.imageViewCheck.setImageDrawable(getDrawable(R.drawable.ic_check))

    }
}