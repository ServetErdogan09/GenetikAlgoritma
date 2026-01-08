package com.serveterdogan.genetikalgoritmamobil.data.repository

import com.serveterdogan.genetikalgoritmamobil.data.remote.FacultyApiService
import com.serveterdogan.genetikalgoritmamobil.domain.model.Faculty
import com.serveterdogan.genetikalgoritmamobil.domain.repository.FacultyRepository
import javax.inject.Inject

class FacultyRepositoryImpl @Inject constructor(
    private val api : FacultyApiService
) : FacultyRepository {

    override suspend fun getFaculties(): List<Faculty> {
        return  api.getFaculties()
    }

    override suspend fun addFaculty(faculty: Faculty): Faculty {
        return  api.addFaculty(faculty)
    }

    override suspend fun deleteFaculty(id: Int) {
        return api.deleteFaculty(id)
    }

    override suspend fun updateFaculty(
        id: Int,
        faculty: Faculty
    ) {
        return api.updateFaculty(id = id , faculty = faculty)
    }


}