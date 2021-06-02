package id.xplicit.capstoneproject.ui.error

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.xplicit.capstoneproject.R
import id.xplicit.capstoneproject.databinding.ActivityErrorBinding

class ErrorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(intent.getIntExtra(ERROR_CODE_EXTRA, -1)) {
            UPLOAD_ERROR_CODE -> {
                binding.tvFailMessage.text = getString(R.string.upload_error_message)
                binding.imgFailed.setImageResource(R.drawable.upload_error)
            }
            DISEASE_NOT_FOUND_ERROR_CODE -> {
                binding.tvFailMessage.text = getString(R.string.disease_not_found_message)
                binding.imgFailed.setImageResource(R.drawable.upload_error)
            }
            NOT_NAIL_OBJECT_ERROR_CODE -> {
                binding.tvFailMessage.text = getString(R.string.nail_object_error_message)
                binding.imgFailed.setImageResource(R.drawable.upload_error)
            }
        }

        binding.btnHome.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val UPLOAD_ERROR_CODE = 101
        const val DISEASE_NOT_FOUND_ERROR_CODE = 102
        const val NOT_NAIL_OBJECT_ERROR_CODE = 103
        const val ERROR_CODE_EXTRA = "error_code_extra"
    }
}