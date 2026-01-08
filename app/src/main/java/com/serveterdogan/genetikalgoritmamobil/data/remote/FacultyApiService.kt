package com.serveterdogan.genetikalgoritmamobil.data.remote

import com.serveterdogan.genetikalgoritmamobil.domain.model.Faculty
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FacultyApiService {

    @GET("/faculty")
    suspend fun getFaculties(): List<Faculty>

    @POST("/faculty")
    suspend fun addFaculty(
        @Body faculty: Faculty
    ): Faculty

    @DELETE("/faculty/{id}")
    suspend fun deleteFaculty(
        @Path("id") id: Int
    )

    @PUT("/faculty/{id}")
    suspend fun updateFaculty(
        @Path("id") id: Int,
        @Body faculty: Faculty
    )
}
