package id.xplicit.capstoneproject.utils

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class ProgressRequestBody(private val file: File, private val content_type: String): RequestBody() {
    private lateinit var listener: UploadCallbacks

    fun setUploadCallbacks(uploadCallbacks: UploadCallbacks) {
        listener = uploadCallbacks
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun contentType(): MediaType? {
        return "$content_type/*".toMediaTypeOrNull()
    }

    override fun writeTo(sink: BufferedSink) {
        val fileLength: Long = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val `in` = FileInputStream(file)
        var uploaded: Long = 0

        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (`in`.read(buffer).also { read = it } != -1) {
                handler.post(ProgressUpdater(uploaded, fileLength))
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        } finally {
            `in`.close()
        }
    }

    inner class ProgressUpdater(private val uploaded: Long, private val total: Long): Runnable {
        override fun run() {
            listener.onProgressUpdate((100 * uploaded / total).toInt())
        }
    }

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
    }
}