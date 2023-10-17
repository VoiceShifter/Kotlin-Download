package com.example.download

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL
import java.nio.file.Paths

class MainActivity : AppCompatActivity() {
    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }

    var imageData = ByteArray(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.DownloadButton)
        val buttonSave = findViewById<Button>(R.id.SaveButton)
        var buffer = byteArrayOf()
        button.setOnClickListener {
            var aEditText = findViewById<EditText>(R.id.editText).toString()
            val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

            // Launch a new coroutine in the scope
            scope.launch {
                val url = URL(aEditText)
                imageData = url.readBytes()
                // TODO: Save the image data to a file or display it in an ImageView
            }
            Toast.makeText(this, "Download process ended", Toast.LENGTH_SHORT).show()

        }





        fun Download(aURL: String): ByteArray {
            val url = URL(aURL)
            return url.readBytes()
        }

        fun mSaveMediaToStorage(bitmap: Bitmap?) {
            val filename = "${System.currentTimeMillis()}.jpg"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }
            fos?.use {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
            }
        }

        fun WriteToDisk(picture: ByteArray) {
            if (picture == ByteArray(0))
            {
                Toast.makeText(this, "Resource is empty", Toast.LENGTH_SHORT).show()
                Log.d("Debug", "Empty string?")
                return
            }
            val bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.size)
            mSaveMediaToStorage(bitmap)
            Log.d("Debug", "Ended writing")
            Toast.makeText(this, "Writing to disk ended", Toast.LENGTH_SHORT).show()

        }

        buttonSave.setOnClickListener {

            val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

            // Launch a new coroutine in the scope
            scope.launch {
                WriteToDisk(imageData)
            }
        }

    }
}