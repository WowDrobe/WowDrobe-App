package app.wowdrobe.com.ktorClient.geocoding


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Scoring(
    @SerializedName("fieldScore")
    val fieldScore: FieldScore?,
    @SerializedName("queryScore")
    val queryScore: Double?
)