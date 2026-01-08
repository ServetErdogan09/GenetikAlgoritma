package com.serveterdogan.genetikalgoritmamobil.data.repository

import com.serveterdogan.genetikalgoritmamobil.data.remote.FacultyApiService
import com.serveterdogan.genetikalgoritmamobil.domain.model.Faculty
import com.serveterdogan.genetikalgoritmamobil.domain.repository.FacultyRepository
import org.jetbrains.annotations.Async
import javax.inject.Inject

class FacultyRepositoryImpl @Inject constructor(
    private val api : FacultyApiService
) : FacultyRepository {
    // Result ile verinin başarılı ya da başarsız geldiğini anlayabileceğiz
    override suspend fun getFaculties(): Result<List<Faculty>> {
        return  try {
            val faculties = api.getFaculties()
            Result.success(faculties)
        }catch (e : Exception){
            Result.failure(e)
        }
    }

    override suspend fun addFaculty(faculty: Faculty): Result<Faculty> {
        return  try {
           val faculty =  api.addFaculty(faculty)
            Result.success(faculty)
        }catch (e : Exception){
            Result.failure(e)
        }
    }

    override suspend fun deleteFaculty(id: Int): Result<Unit> {
        return try {
            Result.success(api.deleteFaculty(id))
        }catch (e : Exception){
            Result.failure(e)
        }
    }

    override suspend fun updateFaculty(
        id: Int,
        faculty: Faculty
    )  : Result<Unit>{
        return try {
            Result.success(api.updateFaculty(id = id , faculty = faculty))
        }catch (e : Exception){
            Result.failure(e)
        }
    }


}