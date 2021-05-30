package id.xplicit.capstoneproject.ui.home

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.xplicit.capstoneproject.entity.RemoteResponse
import id.xplicit.capstoneproject.utils.ApiConfig.getApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel(application: Application): AndroidViewModel(application) {
    private val _isDiseaseFound = MutableLiveData<Boolean>()
    val isDiseaseFound: LiveData<Boolean> = _isDiseaseFound

    private val _isUploadSuccess = MutableLiveData<Boolean>()
    val isUploadSuccess: LiveData<Boolean> = _isUploadSuccess

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

    fun getPrediction(imageUri: Uri) {
        val apiKey = "7a1a1912b05572cc68f598b77e3d17b7"
        val file = File(Environment.getDataDirectory().toString(), imageUri.path ?: "")
        val requestBody = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestBody)
        val client = getApiService().getPredictionResult(apiKey, body)

        client.enqueue(object: Callback<RemoteResponse> {
            override fun onResponse(call: Call<RemoteResponse>, response: Response<RemoteResponse>) {
                _isDiseaseFound.value = false
                _isUploadSuccess.value = false

                if (response.isSuccessful) {
                    _isDiseaseFound.value = response.body()?.diseaseFound
                    _isUploadSuccess.value = true
                } else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RemoteResponse>, t: Throwable) {
                _isUploadSuccess.value = false
                _isDiseaseFound.value = false
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
}