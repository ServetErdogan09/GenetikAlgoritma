package com.serveterdogan.genetikalgoritmamobil.navigation

sealed class Screen(val route : String){
    // parametre taşımadığımız için object oluşturduk
    object  Home : Screen("home_screen")

    // These are for the syllabus screen(sadece ders program için ekranları)
    object Faculty : Screen("faculty_screen")
    object Section : Screen("section_screen")
    object  Teacher : Screen("teacher_screen")
    object  Course : Screen("course_screen")
    object Classroom : Screen("classroom_screen")
    object CourseTeacherAppointment : Screen("course_teacher_appointments_screen")
    object  TeacherAvailability : Screen("teacher_availability_screen")
    object  CreateProgram : Screen("create_program_screen")
    object WeeklySchedule : Screen("weekly_schedule_screen")

    // exam schedule screens(sınav programı ekranları)

}