package app.wowdrobe.com.ktorClient.geocoding


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FieldScore(
    @SerializedName("placeName")
    val placeName: Double?,
    @SerializedName("state")
    val state: Double?
)