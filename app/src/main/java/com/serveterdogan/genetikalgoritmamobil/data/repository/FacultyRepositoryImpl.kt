package com.serveterdogan.genetikalgoritmamobil.data.repository

import com.serveterdogan.genetikalgoritmamobil.data.remote.FacultyApiService
import com.serveterdogan.genetikalgoritmamobil.domain.model.Faculty
import com.serveterdogan.genetikalgoritmamobil.domain.repository.FacultyRepository
import org.jetbrains.annotations.Async
import javax.inject.Inject

/*
repoları bundan dolay ayırdık
api değişirse repoistory hem implementation hem interface oslaydı repository dosyası dğişirdi
ayrı repolar yaptığımız için domain içindeki interface de bir değişiklik olmayacaktı çünkü api ile herhangi bir bağlantısı yok
bundan dolay sadece implementation değişirdi Clean Architecture’da tek bir kural var iç katmanlar dış katmanları bilmez
 */
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
            Result.success( api.addFaculty(faculty))
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