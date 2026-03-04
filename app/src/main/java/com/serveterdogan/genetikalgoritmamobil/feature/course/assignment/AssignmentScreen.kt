package com.serveterdogan.genetikalgoritmamobil.feature.course.assignment

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serveterdogan.genetikalgoritmamobil.domain.model.Course
import com.serveterdogan.genetikalgoritmamobil.domain.model.Teacher
import com.serveterdogan.genetikalgoritmamobil.ui.theme.*
import com.serveterdogan.genetikalgoritmamobil.uiState.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val  departmentName by viewModel.selectedDepartmentName.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = TextTitle,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is UiState.Error -> {
                    AssignmentErrorContent(message = state.message, onRetry = { /* Re-handled by DataStore flow or manual refresh if added */ })

                }
                is UiState.Success -> {
                    AssignmentContent(
                        data = state.data,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onTeacherSelect = viewModel::selectTeacher,
                        onToggleAssignment = viewModel::toggleAssignment,
                        departmentName = departmentName ?: ""
                    )
                }
            }
        }
    }
}

@Composable
private fun AssignmentContent(
    data: AssignmentScreenData,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onTeacherSelect: (Teacher) -> Unit,
    onToggleAssignment: (Int, Boolean) -> Unit,
    departmentName: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // ── 1. Hoca Seçici (Horizontal List) ──
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Groups,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Öğretmen Seçin",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextTitle
            )
            if (data.teachers.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Surface(
                    color = Primary.copy(0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "${data.teachers.size}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }
        }
        
        if (data.teachers.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Bu bölüm için öğretmen bulunamadı.", color = TextMutedAlt, fontSize = 13.sp)
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(data.teachers) { teacher ->
                    val assignedCount = data.allAssignments.count { it.teacherId == teacher.id }
                    TeacherChip(
                        teacher = teacher,
                        isSelected = data.selectedTeacher?.id == teacher.id,
                        assignedCourseCount = assignedCount,
                        onClick = { onTeacherSelect(teacher) }
                    )
                }
            }
        }

        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

        // ── 2. Ders Listesi ──
        if (data.selectedTeacher == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PersonSearch, 
                        contentDescription = null, 
                        tint = Primary.copy(alpha = 0.4f), 
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Lütfen atama yapmak için yukarıdan bir öğretmen seçin.",
                        color = TextMutedAlt,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            val filteredCourses = data.availableCourses.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || it.code.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        placeholder = { Text("Ders ara...", color = TextMutedAlt, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMutedAlt, modifier = Modifier.size(20.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Primary.copy(0.5f),
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
                
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Primary.copy(0.06f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = data.selectedTeacher.name.firstOrNull()?.uppercase() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = data.selectedTeacher.name,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTitle,
                                    fontSize = 14.sp
                                )
                                val assignedCount = data.assignedCourses.size
                                val totalCount = data.availableCourses.size
                                Text(
                                    text = "$assignedCount / $totalCount ders atanmış",
                                    color = TextMutedAlt,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (filteredCourses.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                            Text("Ders bulunamadı.", color = TextMutedAlt)
                        }
                    }
                } else {
                    items(filteredCourses) { course ->
                        val isAssignedToMe = data.assignedCourses.any { it.courseId == course.id }
                        // Bu ders başka bir hocaya atanmış mı kontrol et
                        val otherAssignment = data.allAssignments.find { 
                            it.courseId == course.id && it.teacherId != data.selectedTeacher.id
                        }
                        val ownerTeacherName = if (otherAssignment != null) {
                            data.teachers.find { it.id == otherAssignment.teacherId }?.name
                                ?: "Başka Hoca"
                        } else null

                        CourseAssignmentCard(
                            course = course,
                            isAssigned = isAssignedToMe,
                            ownerTeacherName = ownerTeacherName,
                            onToggle = { checked -> course.id?.let { onToggleAssignment(it, checked) } },
                            departmentName = departmentName
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherChip(
    teacher: Teacher,
    isSelected: Boolean,
    assignedCourseCount: Int = 0,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Primary else Color.White,
        animationSpec = tween(250),
        label = "chipBg"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        modifier = Modifier.shadow(if (isSelected) 6.dp else 1.dp, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            brush = if (isSelected) Brush.linearGradient(listOf(Color.White.copy(0.2f), Color.White.copy(0.2f)))
                            else Brush.linearGradient(listOf(Primary.copy(0.08f), Primary.copy(0.18f)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = teacher.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                        color = if (isSelected) Color.White else Primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                // Atama sayisi badge
                if (assignedCourseCount > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-2).dp),
                        shape = CircleShape,
                        color = if (isSelected) Color.White else Primary
                    ) {
                        Text(
                            text = "$assignedCourseCount",
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Primary else Color.White
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = teacher.name.split(" ").firstOrNull() ?: "",
                color = if (isSelected) Color.White else TextTitle,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CourseAssignmentCard(
    course: Course,
    isAssigned: Boolean,
    ownerTeacherName: String? = null,
    departmentName : String,
    onToggle: (Boolean) -> Unit
) {
    // Ders başka bir hocaya atanmışsa kart devre dışı (disabled)
    val isTakenByOther = ownerTeacherName != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
              color =  if(isTakenByOther)  Primary else Color.White,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isTakenByOther) NeutralSurface else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = course.code,
                        fontWeight = FontWeight.Bold,
                        color = if (isTakenByOther) Color.White else Color.Black,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background( if(isTakenByOther) CodeColor else NeutralSurface, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    // Bölüm adı
                    Text(
                        text = departmentName ,
                        color = TextMutedAlt,
                        fontSize = 12.sp
                    )
                }


                Spacer(Modifier.height(2.dp))
                Text(
                    text = course.name,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isTakenByOther) TextMutedAlt else Color.Black,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = if (isTakenByOther) NeutralSurface else Primary.copy(0.08f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${course.credit} Kredi",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTakenByOther) TextMutedAlt else Primary
                        )
                    }
                    Surface(
                        color = if (isTakenByOther) NeutralSurface else IconBgSecondary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = course.courseType,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTakenByOther) TextMutedAlt else Primary
                        )
                    }
                    if (isAssigned && !isTakenByOther) {
                        Surface(
                            color = Color(0xFF4CAF50).copy(0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    text = "Atandı",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }

                // Başka hocaya atanmışsa uyarı göster
                if (isTakenByOther) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$ownerTeacherName adlı hocaya atanmış",
                            color = Error,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Daire şeklinde checkbox
            val circleColor by animateColorAsState(
                targetValue = when {
                    isTakenByOther -> TextMutedAlt.copy(0.15f)
                    isAssigned -> Primary
                    else -> Color.Transparent
                },
                animationSpec = tween(200),
                label = "circleCheck"
            )
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = when {
                            isTakenByOther -> TextMutedAlt.copy(0.2f)
                            isAssigned -> Primary
                            else -> TextMutedAlt.copy(0.5f)
                        },
                        shape = CircleShape
                    )
                    .background(circleColor)
                    .then(
                        if (!isTakenByOther) Modifier.clickable { onToggle(!isAssigned) }
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isAssigned) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Atandı",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AssignmentErrorContent(message: String, onRetry: () -> Unit) {
    println("Hocaya Tıklanıldığında alınan hata : $message")
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Error, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(16.dp))
            Text(message, color = TextMutedAlt, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text("Tekrar Dene")
            }
        }
    }
}