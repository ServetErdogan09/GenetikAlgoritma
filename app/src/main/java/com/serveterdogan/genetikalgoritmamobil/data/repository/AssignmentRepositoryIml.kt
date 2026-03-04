package com.serveterdogan.genetikalgoritmamobil.data.repository

import com.serveterdogan.genetikalgoritmamobil.data.remote.AssignmentApiService
import com.serveterdogan.genetikalgoritmamobil.domain.model.CourseTeacherAssignment
import com.serveterdogan.genetikalgoritmamobil.domain.repository.AssignmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class AssignmentRepositoryIml @Inject constructor(
    private val apiService: AssignmentApiService
) : AssignmentRepository {

    override suspend fun getAllAssignments(): Result<List<CourseTeacherAssignment>> =
        withContext(Dispatchers.IO) {
            try {
                val assignments = apiService.getAllAssignments()
                Result.success(assignments)
            } catch (e: Exception) {
                Result.failure(Exception(extractErrorMessage(e)))
            }
        }
    
    override suspend fun getAssignmentsByTeacher(teacherId: Int): Result<List<CourseTeacherAssignment>> =
        withContext(Dispatchers.IO) {
            try {
                val assignments = apiService.getAssignmentsByTeacher(teacherId)
                Result.success(assignments)
            } catch (e: Exception) {
                Result.failure(Exception(extractErrorMessage(e)))
            }
        }

    override suspend fun getAssignmentsByCourse(courseId: Int): Result<List<CourseTeacherAssignment>> =
        withContext(Dispatchers.IO) {
            try {
                val assignments = apiService.getAssignmentsByCourse(courseId)
                Result.success(assignments)
            } catch (e: Exception) {
                Result.failure(Exception(extractErrorMessage(e)))
            }
        }

    override suspend fun addAssignment(courseTeacherAssignment: CourseTeacherAssignment): Result<Pair<CourseTeacherAssignment, String>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.addAssignment(courseTeacherAssignment)
                if (response.success && response.data != null) {
                    Result.success(Pair(response.data, response.message ?: "Atama başarıyla yapıldı"))
                } else {
                    Result.failure(Exception(response.message ?: "Atama yapılamadı"))
                }
            } catch (e: Exception) {
                Result.failure(Exception(extractErrorMessage(e)))
            }
        }

    override suspend fun deleteAssignment(id: Int): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteAssignment(id)
                if (response.success) {
                    Result.success(response.message ?: "Atama başarıyla silindi")
                } else {
                    Result.failure(Exception(response.message ?: "Atama silinemedi"))
                }
            } catch (e: Exception) {
                Result.failure(Exception(extractErrorMessage(e)))
            }
        }

    private fun extractErrorMessage(e: Exception): String {
        return try {
            if (e is retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                errorBody?.let { JSONObject(it).getString("error") } ?: e.message()
            } else {
                e.localizedMessage ?: "Bağlantı hatası oluştu"
            }
        } catch (ex: Exception) {
            "Sunucu hatası veya geçersiz veri"
        }
    }
}