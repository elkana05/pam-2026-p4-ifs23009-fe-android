package org.delcom.pam_p4_ifs23009.network.fish.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23009.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFish
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishAdd
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishes

interface IFishRepository {
    suspend fun getAllFishes(
        search: String? = null
    ): ResponseMessage<ResponseFishes?>

    suspend fun postFish(
        nama: RequestBody,
        deskripsi: RequestBody,
        harga: RequestBody,
        asal: RequestBody,
        ukuran: RequestBody,
        masaHidup: RequestBody,
        tingkatKesulitan: RequestBody,
        file: MultipartBody.Part
    ): ResponseMessage<ResponseFishAdd?>

    suspend fun getFishById(
        fishId: String
    ): ResponseMessage<ResponseFish?>

    suspend fun putFish(
        fishId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        harga: RequestBody,
        asal: RequestBody,
        ukuran: RequestBody,
        masaHidup: RequestBody,
        tingkatKesulitan: RequestBody,
        file: MultipartBody.Part? = null
    ): ResponseMessage<String?>

    suspend fun deleteFish(
        fishId: String
    ): ResponseMessage<String?>
}
