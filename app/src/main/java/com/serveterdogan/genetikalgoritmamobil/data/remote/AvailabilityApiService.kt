package com.serveterdogan.genetikalgoritmamobil.data.remote

import com.serveterdogan.genetikalgoritmamobil.domain.model.Availability
import com.serveterdogan.genetikalgoritmamobil.domain.model.BaseResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AvailabilityApiService {

    @GET("/availabilities/teacher/{teacherId}")
    suspend fun getByTeacher(
       @Path("teacherId") teacherId : Int) : BaseResponse<List<Availability>>

    @POST("/availabilities")
    suspend fun add(
      @Body  availability: Availability
    ) : BaseResponse<Availability>


    @PUT("/availabilities/{id}")
    suspend fun update(
        @Path("id") id : Int,
        @Body availability: Availability
    ) : BaseResponse<Availability>

    @DELETE("/availabilities/{id}")
    suspend fun delete(
       @Path("id") id : Int
    ) : BaseResponse<Int>
}