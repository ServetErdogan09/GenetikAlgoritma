package com.serveterdogan.genetikalgoritmamobil.domain.repository

import com.serveterdogan.genetikalgoritmamobil.data.remote.FacultyApiService
import com.serveterdogan.genetikalgoritmamobil.domain.model.Faculty
import javax.inject.Inject


// faculty repository veri kayanğıyla konuşacak viewmodel hangi veri kaynağından(api mi , db mi ) verilerin geldiğini bilmyecek
// veri kaynağından herhangi bir değişiklik olduğu zamna sadece repoda değişiklik olacak
interface  FacultyRepository{
    suspend fun getFaculties() : List<Faculty>
    suspend fun addFaculty(faculty: Faculty) : Faculty
    suspend fun deleteFaculty(id : Int)
    suspend fun updateFaculty(id : Int , faculty: Faculty)

}