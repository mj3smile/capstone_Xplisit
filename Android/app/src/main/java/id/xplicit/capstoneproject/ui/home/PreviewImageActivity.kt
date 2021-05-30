package id.xplicit.capstoneproject.ui.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import id.xplicit.capstoneproject.R
import id.xplicit.capstoneproject.databinding.ActivityPreviewImageBinding
import id.xplicit.capstoneproject.ui.detail.DetailActivity
import id.xplicit.capstoneproject.utils.ViewModelFactory
import java.io.IOException

class PreviewImageActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPreviewImageBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var imageUri: Uri
    private var isImageUriValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory(application)).get(MainViewModel::class.java)
        imageUri = Uri.parse(intent.getStringExtra(IMAGE_URI_EXTRA))
        setPreviewImage(imageUri)

        binding.btnNo.setOnClickListener(this)
        binding.btnYes.setOnClickListener(this)

        viewModel.isDiseaseFound.observe(this, { isDiseaseFound ->
            Toast.makeText(this, "isDiseaseFound: $isDiseaseFound", Toast.LENGTH_SHORT).show()
        })

        viewModel.isUploadSuccess.observe(this, { isUploadSuccess ->
            Toast.makeText(this, "isUploadSuccess: $isUploadSuccess", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onClick(view: View?) {
        when(view) {
            binding.btnNo -> finish()
            binding.btnYes -> {
                if (isImageUriValid) {
                    val intent = Intent(this, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.IMAGE_URI_EXTRA, imageUri.toString())
                    startActivity(intent)

                    finish()
                }
            }
        }
    }

    private fun setPreviewImage(imageUri: Uri) {
        try {
            val pfd: ParcelFileDescriptor? = this.contentResolver.openFileDescriptor(imageUri, "r")
            if (pfd != null) {
                val bitmap = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                if (bitmap != null) {
                    isImageUriValid = true
                    binding.imgPreview.setImageBitmap(bitmap)
                } else {
                    binding.btnNo.visibility = View.GONE
                    binding.btnYes.visibility = View.GONE
                    binding.imgPreview.visibility = View.GONE
                    binding.tvQuestion.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.not_valid_image_message), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        } catch (ex: IOException) {
            Log.e("Error", ex.toString())
            Toast.makeText(this, getString(R.string.not_valid_image_message), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    companion object {
        const val IMAGE_URI_EXTRA = "image_uri_extra"
    }
}