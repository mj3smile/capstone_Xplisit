package id.xplicit.capstoneproject.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import id.xplicit.capstoneproject.BuildConfig
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import id.xplicit.capstoneproject.R
import id.xplicit.capstoneproject.databinding.ActivityMainBinding
import id.xplicit.capstoneproject.utils.ViewModelFactory
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pictureURI: Uri
    private lateinit var picturePath: String
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory(application)).get(MainViewModel::class.java)

        binding.btnUpload.setOnClickListener(this)
        binding.btnTake.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view) {
            binding.btnTake -> {
                takePicture()
            }
            binding.btnUpload -> {
                choosePictureFromStorage()
            }
        }
    }

    private fun takePicture() {
        if (hasCameraPermission() and hasReadStoragePermission() and hasWriteStoragePermission()) {
            val pictureFile: File = viewModel.createImageFile()
            picturePath = pictureFile.absolutePath
            pictureFile.also {
                pictureURI = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    it
                )
                requestTakePicture.launch(pictureURI)
            }
        } else {
            requestPermission.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA))
            takePicture()
        }
    }

    private fun choosePictureFromStorage() {
        if (hasCameraPermission() and hasReadStoragePermission() and hasWriteStoragePermission()) {
            requestChoosePictureFromStorage.launch(arrayOf("image/*"))
        } else {
            requestPermission.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA))
            choosePictureFromStorage()
        }
    }

    private val requestTakePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val intent = Intent(this, PreviewImageActivity::class.java)
            intent.putExtra(PreviewImageActivity.IMAGE_URI_EXTRA, pictureURI.toString())
            intent.putExtra(PreviewImageActivity.IMAGE_PATH_EXTRA, picturePath)
            startActivity(intent)
        }
    }

    private val requestChoosePictureFromStorage = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val intent = Intent(this, PreviewImageActivity::class.java)
            intent.putExtra(PreviewImageActivity.IMAGE_URI_EXTRA, uri.toString())
            startActivity(intent)
        }
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap ->
        resultsMap.forEach {
            if (!it.value) {
                Toast.makeText(this, getString(R.string.rejected_permission_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    private fun hasWriteStoragePermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    private fun hasReadStoragePermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}