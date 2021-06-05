package id.xplicit.capstoneproject.ui.home

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.xplicit.capstoneproject.entity.RemoteResponse
import id.xplicit.capstoneproject.utils.ApiConfig.getApiService
import id.xplicit.capstoneproject.utils.ProgressRequestBody
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel(application: Application): AndroidViewModel(application) {
    private val _isDiseaseFound = MutableLiveData<Boolean>()
    val isDiseaseFound: LiveData<Boolean> = _isDiseaseFound

    private val _isNailRecognized = MutableLiveData<Boolean>()
    val isNailRecognized: LiveData<Boolean> = _isNailRecognized

    private val _isUploadSuccess = MutableLiveData<Boolean>()
    val isUploadSuccess: LiveData<Boolean> = _isUploadSuccess

    private val _uploadProgress = MutableLiveData<Int>()
    val uploadProgress: LiveData<Int> = _uploadProgress

    private val _predictionResult = MutableLiveData<RemoteResponse>()
    val predictionResult: LiveData<RemoteResponse> = _predictionResult

    fun createImageFile(): File {
        val context = getApplication<Application>().applicationContext
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            timeStamp,
            ".jpg",
            storageDir
        )
    }

    fun getPrediction(imageUri: Uri?, imagePath: String?) {
        val file: File?

        if (imagePath == null && imageUri != null) {
            val context = getApplication<Application>().applicationContext
            file = createImageFile()
            val inputStream = context.contentResolver.openInputStream(imageUri)
            copyStreamToFile(inputStream, file)
        } else {
            file = File(imagePath ?: "")
        }

        val requestBody = ProgressRequestBody(file, "image")
        requestBody.setUploadCallbacks(object: ProgressRequestBody.UploadCallbacks {
            override fun onProgressUpdate(percentage: Int) {
                _uploadProgress.value = percentage
                Log.i(ContentValues.TAG, "progressUpload: ${percentage}%")
            }
        })

        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestBody)
        val client = getApiService().getPredictionResult(body)

        client.enqueue(object: Callback<RemoteResponse> {
            override fun onResponse(call: Call<RemoteResponse>, response: Response<RemoteResponse>) {
                if (response.isSuccessful) {
                    _isDiseaseFound.value = response.body()?.isDiseaseMatch
                    _isNailRecognized.value = response.body()?.isNail
                    _predictionResult.value = response.body()
                    _isUploadSuccess.value = true
                    _uploadProgress.value = 100
                } else {
                    _isUploadSuccess.value = false
                    _isDiseaseFound.value = false
                    _uploadProgress.value = 100
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RemoteResponse>, t: Throwable) {
                _isUploadSuccess.value = false
                _isDiseaseFound.value = false
                _uploadProgress.value = 100
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    private fun copyStreamToFile(inputStream: InputStream?, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024)
                while (true) {
                    val byteCount = input?.read(buffer)
                    if (byteCount != null) {
                        if (byteCount < 0) break
                        output.write(buffer, 0, byteCount)
                    }
                }
                output.flush()
            }
        }
    }
}