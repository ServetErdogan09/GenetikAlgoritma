package com.serveterdogan.genetikalgoritmamobil.data.remote

import com.serveterdogan.genetikalgoritmamobil.domain.model.BaseResponse
import com.serveterdogan.genetikalgoritmamobil.domain.model.ClassRoom
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ClassroomApiService {

    // tüm sınıfları getir
    @GET("/classrooms")
    suspend fun getAllClassRoom() : BaseResponse<List<ClassRoom>>


    //Yeni sınıf ekle
    @POST("/classrooms")
    suspend fun addClassRoom(
        @Body classRoom: ClassRoom
    ) : BaseResponse<ClassRoom>


    // sınıf güncele
    @PUT("/classrooms/{id}")
    suspend fun updateClass(
        @Body classRoom: ClassRoom,
        @Path("id") id : Int
    ) : BaseResponse<ClassRoom>

    //sınıf sil
    @DELETE("/classrooms/{id}")
    suspend fun deleteClass(
        @Path("id") id : Int
    ): BaseResponse<Int>
}