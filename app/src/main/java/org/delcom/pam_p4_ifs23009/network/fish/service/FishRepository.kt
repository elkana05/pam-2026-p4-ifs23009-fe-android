package org.delcom.pam_p4_ifs23009.network.fish.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23009.helper.SuspendHelper
import org.delcom.pam_p4_ifs23009.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFish
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishAdd
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishes

class FishRepository(private val fishApiService: FishApiService) : IFishRepository {
    override suspend fun getAllFishes(search: String?): ResponseMessage<ResponseFishes?> {
        return SuspendHelper.safeApiCall {
            fishApiService.getAllFishes(search)
        }
    }

    override suspend fun postFish(
        nama: RequestBody,
        deskripsi: RequestBody,
        harga: RequestBody,
        asal: RequestBody,
        ukuran: RequestBody,
        masaHidup: RequestBody,
        tingkatKesulitan: RequestBody,
        file: MultipartBody.Part
    ): ResponseMessage<ResponseFishAdd?> {
        return SuspendHelper.safeApiCall {
            fishApiService.postFish(
                nama = nama,
                deskripsi = deskripsi,
                harga = harga,
                asal = asal,
                ukuran = ukuran,
                masaHidup = masaHidup,
                tingkatKesulitan = tingkatKesulitan,
                file = file
            )
        }
    }

    override suspend fun getFishById(fishId: String): ResponseMessage<ResponseFish?> {
        return SuspendHelper.safeApiCall {
            fishApiService.getFishById(fishId)
        }
    }

    override suspend fun putFish(
        fishId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        harga: RequestBody,
        asal: RequestBody,
        ukuran: RequestBody,
        masaHidup: RequestBody,
        tingkatKesulitan: RequestBody,
        file: MultipartBody.Part?
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            fishApiService.putFish(
                fishId = fishId,
                nama = nama,
                deskripsi = deskripsi,
                harga = harga,
                asal = asal,
                ukuran = ukuran,
                masaHidup = masaHidup,
                tingkatKesulitan = tingkatKesulitan,
                file = file
            )
        }
    }

    override suspend fun deleteFish(fishId: String): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            fishApiService.deleteFish(fishId)
        }
    }
}
