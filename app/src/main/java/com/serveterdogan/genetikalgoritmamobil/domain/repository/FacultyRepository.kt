package com.serveterdogan.genetikalgoritmamobil.domain.repository

import com.serveterdogan.genetikalgoritmamobil.data.remote.FacultyApiService
import javax.inject.Inject


// faculty repository veri kayanğıyla konuşacak viewmodel hangi veri kaynağından(api mi , db mi ) verilerin geldiğini bilmyecek
// veri kaynağından herhangi bir değişiklik olduğu zamna sadece repoda değişiklik olacak
class FacultyRepository @Inject constructor(
    private val api : FacultyApiService
) {

}