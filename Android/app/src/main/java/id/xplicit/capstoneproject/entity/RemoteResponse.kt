package id.xplicit.capstoneproject.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class RemoteResponse(
	@field:Json(name="is_nail")
	val isNail: Boolean,

	@field:Json(name="treat")
	val treat: String,

	@field:Json(name="is_disease_match")
	val isDiseaseMatch: Boolean,

	@field:Json(name="name")
	val name: String,

	@field:Json(name="accuracy")
	val accuracy: String,

	@field:Json(name="desc")
	val desc: String
): Parcelable

