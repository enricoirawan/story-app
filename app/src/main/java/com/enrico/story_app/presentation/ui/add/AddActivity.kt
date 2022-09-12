package com.enrico.story_app.presentation.ui.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.enrico.story_app.R
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.databinding.ActivityAddBinding
import com.enrico.story_app.presentation.ui.ViewModelFactory
import com.enrico.story_app.utils.Constant
import com.enrico.story_app.utils.createCustomTempFile
import com.enrico.story_app.utils.reduceFileImage
import com.enrico.story_app.utils.uriToFile
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val factory: ViewModelFactory = ViewModelFactory.getInstance()
    private val addViewModel: AddViewModel by viewModels {
        factory
    }

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.openGalleryBtn.setOnClickListener{
            startGallery()
        }
        binding.takePhotoBtn.setOnClickListener{
            startTakePhoto()
        }
        binding.uploadBtn.setOnClickListener{
            if(binding.descriptionEd.text.toString().isNotEmpty()){
                uploadImage()
            }else {
                Snackbar.make(binding.root, R.string.description_required, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorError, theme))
                    .show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddActivity,
                "com.enrico.story_app",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewIv.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddActivity)

            getFile = myFile

            binding.previewIv.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding.descriptionEd.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            sharedPreferences = getSharedPreferences(Constant.preferencesName, Context.MODE_PRIVATE)
            val token = sharedPreferences.getString(Constant.preferencesToken, "")!!

            addViewModel.uploadStory(token, imageMultipart, description).observe(this) { result ->
                if (result != null) {
                    when(result){
                        is ResultState.Loading -> {
                            binding.uploadBtn.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is ResultState.Success -> {
                            Snackbar.make(binding.root, R.string.success_upload, Snackbar.LENGTH_LONG)
                                .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorPrimary, theme))
                                .show()

                            Handler(Looper.getMainLooper()).postDelayed({
                                setResult(RESULT_OK)
                                finish()
                            }, Constant.delay)
                        }
                        is ResultState.Error -> {
                            binding.uploadBtn.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(binding.root, result.error, Snackbar.LENGTH_LONG)
                                .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorError, theme))
                                .show()
                        }
                    }
                }
            }
        } else {
            Snackbar.make(binding.root, R.string.file_not_ready, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorError, theme))
                .show()
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}