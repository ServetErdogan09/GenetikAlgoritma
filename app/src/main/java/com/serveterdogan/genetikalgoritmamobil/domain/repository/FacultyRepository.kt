package com.serveterdogan.genetikalgoritmamobil.domain.repository

import com.serveterdogan.genetikalgoritmamobil.data.remote.FacultyApiService
import com.serveterdogan.genetikalgoritmamobil.domain.model.Faculty
import javax.inject.Inject


// faculty repository veri kayanğıyla konuşacak viewmodel hangi veri kaynağından(api mi , db mi ) verilerin geldiğini bilmyecek
// veri kaynağından herhangi bir değişiklik olduğu zamna sadece repoda değişiklik olacak
interface  FacultyRepository{
    suspend fun getFaculties() : Result<List<Faculty>>
    suspend fun addFaculty(faculty: Faculty) : Result<Faculty>
    suspend fun deleteFaculty(id : Int) : Result<Unit>
    suspend fun updateFaculty(id : Int , faculty: Faculty) : Result<Unit>

}