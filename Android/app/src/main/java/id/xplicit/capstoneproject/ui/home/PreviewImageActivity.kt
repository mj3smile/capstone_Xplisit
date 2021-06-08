package id.xplicit.capstoneproject.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import id.xplicit.capstoneproject.R
import id.xplicit.capstoneproject.databinding.ActivityPreviewImageBinding
import id.xplicit.capstoneproject.ui.detail.DetailActivity
import id.xplicit.capstoneproject.ui.error.ErrorActivity
import id.xplicit.capstoneproject.utils.ViewModelFactory
import java.io.IOException

class PreviewImageActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPreviewImageBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var imageUri: Uri
    private var imagePath: String? = null
    private var isImageUriValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory(application)).get(MainViewModel::class.java)
        imageUri = Uri.parse(intent.getStringExtra(IMAGE_URI_EXTRA))
        imagePath = intent.getStringExtra(IMAGE_PATH_EXTRA)
        setPreviewImage(imageUri)

        binding.btnFalse.setOnClickListener(this)
        binding.btnTrue.setOnClickListener(this)

        viewModel.uploadProgress.observe(this, { uploadProgress ->
            binding.progressBar.visibility = View.VISIBLE
            setUploadProgress(binding.progressBar, uploadProgress)
        })
    }

    override fun onClick(view: View?) {
        when(view) {
            binding.btnFalse -> finish()
            binding.btnTrue -> {
                if (isImageUriValid) {
                    if (imagePath != null) {
                        viewModel.getPrediction(null, imagePath)
                    } else {
                        viewModel.getPrediction(imageUri, null)
                    }
                    showUploadProgress()
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
                    binding.previewImageCard.visibility = View.GONE
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

    private fun showUploadProgress() {
        binding.previewImageCard.visibility = View.GONE
        binding.previewImageLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
        binding.tvProgressStatus.visibility = View.VISIBLE

        binding.progressBar.max = 100 * 100
    }

    private fun setUploadProgress(progressBar: ProgressBar, progressTo: Int) {
        val animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, 100 * progressTo)
        animation.duration = 5000
        animation.setAutoCancel(true)
        animation.interpolator = DecelerateInterpolator()
        animation.start()

        if (progressTo == 100) {
            animation.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    if (viewModel.isUploadSuccess.value == true) {
                        if (viewModel.isNailRecognized.value == true) {
                            if (viewModel.isDiseaseFound.value == false) {
                                val intent = Intent(this@PreviewImageActivity, ErrorActivity::class.java)
                                intent.putExtra(ErrorActivity.ERROR_CODE_EXTRA, ErrorActivity.DISEASE_NOT_FOUND_ERROR_CODE)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this@PreviewImageActivity, DetailActivity::class.java)
                                intent.putExtra(DetailActivity.IMAGE_URI_EXTRA, imageUri.toString())
                                intent.putExtra(DetailActivity.RESPONSE_EXTRA, viewModel.predictionResult.value)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val intent = Intent(this@PreviewImageActivity, ErrorActivity::class.java)
                            intent.putExtra(ErrorActivity.ERROR_CODE_EXTRA, ErrorActivity.NOT_NAIL_OBJECT_ERROR_CODE)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val intent = Intent(this@PreviewImageActivity, ErrorActivity::class.java)
                        intent.putExtra(ErrorActivity.ERROR_CODE_EXTRA, ErrorActivity.UPLOAD_ERROR_CODE)
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }
    }

    companion object {
        const val IMAGE_URI_EXTRA = "image_uri_extra"
        const val IMAGE_PATH_EXTRA = "image_path_extra"
    }
}