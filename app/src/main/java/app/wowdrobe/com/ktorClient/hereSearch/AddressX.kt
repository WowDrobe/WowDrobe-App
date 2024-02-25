package app.wowdrobe.com.ktorClient.hereSearch


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AddressX(
    @SerializedName("label")
    val label: List<Label?>?
)