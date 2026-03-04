package com.serveterdogan.genetikalgoritmamobil.feature.course.assignment

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serveterdogan.genetikalgoritmamobil.data.UserPreferencesDataStore
import com.serveterdogan.genetikalgoritmamobil.domain.model.Course
import com.serveterdogan.genetikalgoritmamobil.domain.model.CourseTeacherAssignment
import com.serveterdogan.genetikalgoritmamobil.domain.repository.AssignmentRepository
import com.serveterdogan.genetikalgoritmamobil.domain.repository.CourseRepository
import com.serveterdogan.genetikalgoritmamobil.uiState.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.serveterdogan.genetikalgoritmamobil.domain.model.Teacher
import com.serveterdogan.genetikalgoritmamobil.domain.repository.TeacherRepository

data class AssignmentScreenData(
    val teachers: List<Teacher> = emptyList(),
    val selectedTeacher: Teacher? = null,
    val availableCourses: List<Course> = emptyList(),
    // Seçili hocanın atamaları
    val assignedCourses: List<CourseTeacherAssignment> = emptyList(),
    // TÜM atamalar — hangi ders kime atanmış bilgisi için
    val allAssignments: List<CourseTeacherAssignment> = emptyList()
)

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val assignmentRepository: AssignmentRepository,
    private val courseRepository: CourseRepository,
    private val teacherRepository: TeacherRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AssignmentScreenData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage = _userMessage.asStateFlow()

    private var currentTeacher: Teacher? = null
    private var cachedTeachers: List<Teacher> = emptyList()
    private var cachedAvailableCourses: List<Course> = emptyList()
    private var cachedAssignedCourses: List<CourseTeacherAssignment> = emptyList()
    // Veritabanındaki tüm atamalar (hangi ders hangi hocada)
    private var cachedAllAssignments: List<CourseTeacherAssignment> = emptyList()


    val selectedDepartmentName: StateFlow<String?> = userPreferencesDataStore.userSelectionFlow
        .map { it?.departmentName }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _departmentName = MutableStateFlow<String?>("")
    var departmentName : StateFlow<String?> = _departmentName

    init {
        viewModelScope.launch {
            userPreferencesDataStore.userSelectionFlow.collect { selection ->
                val deptId = selection?.departmentId
                val year = selection?.year
                if (deptId != null && year != null) {
                    loadData(deptId, year)
                } else {
                    cachedTeachers = emptyList()
                    cachedAvailableCourses = emptyList()
                    currentTeacher = null
                    cachedAllAssignments = emptyList()
                    updateSuccessState()
                }
            }
        }
    }

    private fun updateSuccessState() {
        _uiState.value = UiState.Success(
            AssignmentScreenData(
                teachers = cachedTeachers,
                selectedTeacher = currentTeacher,
                availableCourses = cachedAvailableCourses,
                assignedCourses = cachedAssignedCourses,
                allAssignments = cachedAllAssignments
            )
        )
    }

    private suspend fun loadData(departmentId: Int, year: Int) {
        _uiState.value = UiState.Loading
        
        // Bölüme ait öğretmenleri getir
        val teacherResult = teacherRepository.getByDepartmentId(departmentId)
        if (teacherResult.isSuccess) {
            cachedTeachers = teacherResult.getOrDefault(emptyList())
        }

        // Bölüm ve yıla ait dersleri getir
        val courseResult = courseRepository.getByDepartmentAndYear(departmentId, year)
        if (courseResult.isSuccess) {
            cachedAvailableCourses = courseResult.getOrDefault(emptyList())
        }

        // Tüm atamaları getir (hangi ders kime atanmış bilgisi)
        val allAssignmentsResult = assignmentRepository.getAllAssignments()
        if (allAssignmentsResult.isSuccess) {
            cachedAllAssignments = allAssignmentsResult.getOrDefault(emptyList())
        }

        updateSuccessState()
    }

    /**
     * Ekranda bir hoca seçildiğinde onun atamalarını çeker.
     */
    fun selectTeacher(teacher: Teacher) {
        currentTeacher = teacher
        val teacherId = teacher.id ?: return
        
        // Seçili hocanın atamalarını tüm atamalardan filtrele (ekstra API çağrısı yok)
        cachedAssignedCourses = cachedAllAssignments.filter { it.teacherId == teacherId }
        updateSuccessState()
    }

    /**
     * Öğretmene ders ekleme ve çıkarma (Onay kutusu tıklandığında)
     */
    fun toggleAssignment(courseId: Int, isAssigned: Boolean) {
        val teacherId = currentTeacher?.id ?: return
        
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            if (isAssigned) {
                val newAssignment = CourseTeacherAssignment(
                    courseId = courseId,
                    teacherId = teacherId
                )
                val result = assignmentRepository.addAssignment(newAssignment)
                if (result.isSuccess) {
                    val (savedAssignment, message) = result.getOrNull()!!
                    cachedAssignedCourses = cachedAssignedCourses + savedAssignment
                    // Tüm atamalar listesine de ekle
                    cachedAllAssignments = cachedAllAssignments + savedAssignment
                    updateSuccessState()
                    _userMessage.value = message
                } else {
                    _userMessage.value = result.exceptionOrNull()?.message ?: "Atama yapılamadı"
                    updateSuccessState()
                }
            } else {
                val assignmentToDelete = cachedAssignedCourses.find { it.courseId == courseId && it.teacherId == teacherId }
                if (assignmentToDelete?.id != null) {
                    val result = assignmentRepository.deleteAssignment(assignmentToDelete.id)
                    if (result.isSuccess) {
                        cachedAssignedCourses = cachedAssignedCourses.filter { it.id != assignmentToDelete.id }
                        // Tüm atamalar listesinden de sil
                        cachedAllAssignments = cachedAllAssignments.filter { it.id != assignmentToDelete.id }
                        updateSuccessState()
                        _userMessage.value = result.getOrDefault("Atama silindi")
                    } else {
                        _userMessage.value = result.exceptionOrNull()?.message ?: "Atama silinirken hata oluştu"
                        println("Hata silinemedi :${result.exceptionOrNull()?.message ?: "Atama silinirken hata oluştu"}")
                        updateSuccessState()
                    }
                } else {
                     updateSuccessState()
                }
            }
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}