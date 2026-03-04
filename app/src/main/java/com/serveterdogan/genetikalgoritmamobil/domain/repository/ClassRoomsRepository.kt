package com.serveterdogan.genetikalgoritmamobil.domain.repository

import com.serveterdogan.genetikalgoritmamobil.domain.model.ClassRoom

interface ClassRoomsRepository {

    suspend fun getClassRooms() : Result<List<ClassRoom>>


    suspend fun addClassRoom(classRoom: ClassRoom) : Result<Pair<ClassRoom , String>>

    suspend fun deleteClassRoom(id : Int) : Result<String>

    suspend fun updateClassRoom(id : Int , classRoom: ClassRoom) : Result<String?>

}
