package id.xplicit.capstoneproject.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class RemoteResponse(
	@field:Json(name="disease_found")
	val diseaseFound: Boolean,

	@field:Json(name="error")
	val error: String,

	@field:Json(name="message")
	val message: Message,

	@field:Json(name="status")
	val status: String
)

@Parcelize
data class Message(
	@field:Json(name="treatment_tips")
	val treatmentTips: String,

	@field:Json(name="description")
	val description: String,

	@field:Json(name="uploaded_picture_url")
	val uploadedPictureUrl: String,

	@field:Json(name="disease_name")
	val diseaseName: String
): Parcelable

