package com.serveterdogan.genetikalgoritmamobil.domain.repository

import com.serveterdogan.genetikalgoritmamobil.domain.model.CourseTeacherAssignment

interface AssignmentRepository {

    // Tüm atamaları getir (hangi ders kime atanmış bilgisi)
    suspend fun getAllAssignments(): Result<List<CourseTeacherAssignment>>

    suspend fun getAssignmentsByTeacher(teacherId : Int) : Result<List<CourseTeacherAssignment>>

    suspend fun getAssignmentsByCourse(courseId : Int) : Result<List<CourseTeacherAssignment>>

    suspend fun addAssignment(courseTeacherAssignment: CourseTeacherAssignment) : Result<Pair<CourseTeacherAssignment , String>>

    suspend fun deleteAssignment(id : Int) : Result<String>

}