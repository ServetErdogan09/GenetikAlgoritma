package com.serveterdogan.genetikalgoritmamobil.di
import com.serveterdogan.genetikalgoritmamobil.data.remote.AssignmentApiService
import com.serveterdogan.genetikalgoritmamobil.data.remote.AvailabilityApiService
import com.serveterdogan.genetikalgoritmamobil.data.remote.ClassroomApiService
import com.serveterdogan.genetikalgoritmamobil.data.remote.CourseApiService
import com.serveterdogan.genetikalgoritmamobil.data.remote.DepartmentApiService
import com.serveterdogan.genetikalgoritmamobil.data.remote.FacultyApiService
import com.serveterdogan.genetikalgoritmamobil.data.remote.TeacherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }



    @Provides
    @Singleton
    fun provideRetrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideFacultyApiService(
        retrofit: Retrofit
    ): FacultyApiService {
        return retrofit.create(FacultyApiService::class.java)
    }
    
    
    @Provides
    @Singleton
    fun provideDepartmentApiService(
        retrofit: Retrofit
    ): DepartmentApiService{
        return retrofit.create(DepartmentApiService::class.java)
    }


    @Provides
    @Singleton
    fun providerTeacherApiService(
        retrofit: Retrofit
    ): TeacherApiService{
        return retrofit.create(TeacherApiService::class.java)
    }


    @Provides
    @Singleton
    fun providerCourseApiService(
        retrofit: Retrofit
    ): CourseApiService{
       return retrofit.create(CourseApiService::class.java)
    }


    @Provides
    @Singleton
    fun providerClassroomApiService(
        retrofit: Retrofit
    ): ClassroomApiService{
        return  retrofit.create(ClassroomApiService::class.java)
    }


    @Provides
    @Singleton
    fun providerAssignmentApiService(
        retrofit: Retrofit
    ): AssignmentApiService
    {
        return  retrofit.create(AssignmentApiService::class.java)
    }

    @Provides
    @Singleton
    fun providerAvailabilityApiService(
        retrofit: Retrofit
    ): AvailabilityApiService
    {
        return  retrofit.create(AvailabilityApiService::class.java)
    }

}
