package org.delcom.pam_p4_ifs23009.network.fish.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.delcom.pam_p4_ifs23009.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFish
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishAdd
import org.delcom.pam_p4_ifs23009.network.fish.data.ResponseFishes
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FishApiService {
    @GET("fish")
    suspend fun getAllFishes(
        @Query("search") search: String? = null
    ): ResponseMessage<ResponseFishes?>

    @Multipart
    @POST("fish")
    suspend fun postFish(
        @Part("name") nama: RequestBody,
        @Part("description") deskripsi: RequestBody,
        @Part("price") harga: RequestBody,
        @Part("origin") asal: RequestBody,
        @Part("size") ukuran: RequestBody,
        @Part("lifespan") masaHidup: RequestBody,
        @Part("difficulty") tingkatKesulitan: RequestBody,
        @Part file: MultipartBody.Part
    ): ResponseMessage<ResponseFishAdd?>

    @GET("fish/{fishId}")
    suspend fun getFishById(
        @Path("fishId") fishId: String
    ): ResponseMessage<ResponseFish?>

    @GET("fish/{fishId}/image")
    suspend fun getFishImageById(
        @Path("fishId") fishId: String
    ): ResponseBody

    @Multipart
    @PUT("fish/{fishId}")
    suspend fun putFish(
        @Path("fishId") fishId: String,
        @Part("name") nama: RequestBody,
        @Part("description") deskripsi: RequestBody,
        @Part("price") harga: RequestBody,
        @Part("origin") asal: RequestBody,
        @Part("size") ukuran: RequestBody,
        @Part("lifespan") masaHidup: RequestBody,
        @Part("difficulty") tingkatKesulitan: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): ResponseMessage<String?>

    @DELETE("fish/{fishId}")
    suspend fun deleteFish(
        @Path("fishId") fishId: String
    ): ResponseMessage<String?>
}
