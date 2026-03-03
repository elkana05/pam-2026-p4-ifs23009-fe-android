package org.delcom.pam_p4_ifs23009.network.fish.data

import com.google.gson.annotations.SerializedName

data class ResponseFishes (
    val fishes: List<ResponseFishData>? = emptyList()
)

data class ResponseFish (
    val fish: ResponseFishData? = null
)

data class ResponseFishAdd (
    @SerializedName("fish_id") val fishId: String? = null
)

data class ResponseFishData(
    val id: String,
    @SerializedName("name") val nama: String? = "",
    @SerializedName("description") val deskripsi: String? = "",
    @SerializedName("price") val harga: String? = "",
    @SerializedName("origin") val asal: String? = "",
    @SerializedName("size") val ukuran: String? = "",
    @SerializedName("lifespan") val masaHidup: String? = "",
    @SerializedName("difficulty") val tingkatKesulitan: String? = "",
    @SerializedName("created_at") val createdAt: String? = "",
    @SerializedName("updated_at") val updatedAt: String? = ""
)
