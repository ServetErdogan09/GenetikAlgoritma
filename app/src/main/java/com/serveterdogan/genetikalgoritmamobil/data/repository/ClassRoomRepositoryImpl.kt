package com.serveterdogan.genetikalgoritmamobil.data.repository

import com.serveterdogan.genetikalgoritmamobil.data.remote.ClassroomApiService
import com.serveterdogan.genetikalgoritmamobil.domain.model.BaseResponse
import com.serveterdogan.genetikalgoritmamobil.domain.model.ClassRoom
import com.serveterdogan.genetikalgoritmamobil.domain.repository.ClassRoomsRepository
import javax.inject.Inject

class ClassRoomRepositoryImpl @Inject constructor(
    private val api : ClassroomApiService
)  : ClassRoomsRepository{
    override suspend fun getClassRooms(): Result<List<ClassRoom>> {
      return try {
            val response  = api.getAllClassRoom()

          if(response.success && response.data != null){
              Result.success(response.data)
          }else{
              Result.failure(Exception(response.message ?: "Veriler yüklenemedi"))
          }
        }catch (e : Exception){
            println("hata class : ${e.message}")
            Result.failure(e)
        }
    }


    override suspend fun addClassRoom(classRoom: ClassRoom): Result<Pair<ClassRoom , String>> {
       return try {
          val response : BaseResponse<ClassRoom> = api.addClassRoom(classRoom)
          if(response.success && response.data != null){
              Result.success(Pair(response.data , response.message ?: "${response.data.name} başarıyla oluşturuldu"))
          }else{
              Result.failure(Exception(response.message ?: "Ekleme Başarsız"))
          }
       }catch (e: Exception){
           println("Hata Repository: ${e.message}")
           Result.failure(e)
       }
    }

    override suspend fun deleteClassRoom(id: Int): Result<String> {
        return try {
            val response: BaseResponse<Int> = api.deleteClass(id)
            if(response.success){
                Result.success(response.message?: "Silme Başarılı")
            }else{
                Result.failure(Exception(response.message ?: "Silme sırasında hata oluştu"))
            }
        }catch (e : Exception){
            println("Hata Repository: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateClassRoom(
        id: Int,
        classRoom: ClassRoom
    ): Result<String> {
        return try {
            val response : BaseResponse<ClassRoom>  = api.updateClass(id = id, classRoom = classRoom)
            if (response.success){
                Result.success(response.message ?: "Güncelleme işlemi başarılı")            }else{
                Result.failure(Exception(response.message ?: "Günceleme işlemi başarsız"))
            }
        }catch (e: Exception){
            println("Hata Repository: ${e.message}")
            Result.failure(e)
        }
    }


}