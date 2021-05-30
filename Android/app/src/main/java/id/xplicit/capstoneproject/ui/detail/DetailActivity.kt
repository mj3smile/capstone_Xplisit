package id.xplicit.capstoneproject.ui.detail

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import id.xplicit.capstoneproject.R
import id.xplicit.capstoneproject.databinding.ActivityDetailBinding
import java.io.IOException
import kotlin.math.abs

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        val imageUri = Uri.parse(intent.getStringExtra(IMAGE_URI_EXTRA))
        setLayoutBackground(imageUri)

        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                verticalOffset == 0 -> {
                    binding.toolbarLayout.title = ""
                    binding.wrapperDescLayout.setBackgroundResource(R.drawable.layout_bg)
                }
                abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                    binding.toolbarLayout.title = getString(
                        R.string.detail_activity_toolbar_title
                    )
                    binding.wrapperDescLayout.setBackgroundResource(R.drawable.layout_bg_noradius)
                }
            }
        })
    }

    private fun setLayoutBackground(imageUri: Uri) {
        try {
            val pfd: ParcelFileDescriptor? = this.contentResolver.openFileDescriptor(imageUri, "r")
            if (pfd != null) {
                val bitmap = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                if (bitmap != null) {
                    val background = BitmapDrawable(this.resources, bitmap)
                    binding.baseLayout.background = background
                } else {
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