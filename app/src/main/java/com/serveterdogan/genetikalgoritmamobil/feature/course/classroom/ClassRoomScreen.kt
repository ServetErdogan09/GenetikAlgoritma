package com.serveterdogan.genetikalgoritmamobil.feature.course.classroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.serveterdogan.genetikalgoritmamobil.domain.model.ClassRoom
import com.serveterdogan.genetikalgoritmamobil.ui.theme.*
import com.serveterdogan.genetikalgoritmamobil.uiState.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassRoomScreen(
    onNavigateToAddClassRoom: () -> Unit = {},
    viewModel: ClassRoomViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearUserMessage()
        }
    }

    val filterTypes = listOf("Tümü", "Amfi", "Laboratuvar", "Sınıf")
    var classToDelete by remember { mutableStateOf<ClassRoom?>(null) }
    var showAddSheet by remember { mutableStateOf(false) }
    var editingClassRoom by remember { mutableStateOf<ClassRoom?>(null) }


    Scaffold(
        containerColor = ScreenBackground,
        snackbarHost = {}, // Boş bırakıyoruz ki Scaffold kendisi konumlandırmasın

        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(end = 8.dp).shadow(8.dp, RoundedCornerShape(16.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Derslik Ekle", modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Arama Çubuğu
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    placeholder = { Text("Derslik ara...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                // Filtre Chip'leri
                LazyRow(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterTypes) { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onTypeSelected(type) },
                            label = {
                                Text(
                                    type,
                                    color = if (isSelected) Color.White else TextSubtitle
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                // Liste
                when (state) {
                    is UiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Primary)
                        }
                    }
                    is UiState.Error -> {
                        val message = (state as UiState.Error).message
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(message, color = Error, textAlign = TextAlign.Center)
                        }
                    }
                    is UiState.Success -> {
                        val classrooms = (state as UiState.Success).data
                        if (classrooms.isEmpty()) {
                            EmptyListContent()
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(classrooms) { classroom ->
                                    ClassroomCard(
                                        classroom = classroom,
                                        onEdit = {
                                            editingClassRoom = classroom
                                        },
                                        onDelete = {
                                            classToDelete = classroom
                                        }

                                    )
                                }
                            }
                        }
                    }


                }

                if(classToDelete != null){
                    ClassAlertDialog(
                        className = classToDelete?.name ?: "",
                        onDismiss = {classToDelete = null},
                        onConfirm = {
                            classToDelete?.id.let { viewModel.deleteClass(id = classToDelete?.id ?: 0)
                                classToDelete = null
                            }
                        }
                    )
                }
            }

            // Snackbar'ı en alta sabitlemek için buraya ekliyoruz
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp) // En alttan biraz boşluk
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }

    if (showAddSheet) {
        AddClassRoomSheet(
            onDismiss = { showAddSheet = false },
            onConfirm = { room ->
                viewModel.addClassRoom(room)
                showAddSheet = false
            }
        )
    }

    editingClassRoom?.let { room ->
        EditClassRoomSheet(
            classRoom = room,
            onDismiss = { editingClassRoom = null },
            onConfirm = { updatedRoom ->
                viewModel.updateClass(id = room.id, classRoom = updatedRoom)
                editingClassRoom = null
            }
        )
    }
}

@Composable
fun ClassroomCard(
    classroom: ClassRoom,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val iconInfo = when (classroom.type) {
        "Amfi" -> Icons.Default.TheaterComedy to IconBgBlue
        "Laboratuvar" -> Icons.Default.Science to IconBgRed
        else -> Icons.Default.School to IconBgGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // İkon Kutusu
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconInfo.second),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconInfo.first,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Bilgiler
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = classroom.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextTitle
                    )
                    Text(
                        text = "${classroom.capacity} Kişilik",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSubtitle
                    )
                }

                // Badge (Kategori Etiketi)
                Surface(
                    color = iconInfo.second.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = classroom.type.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Divider(color = DividerColor, thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))



            // Action Butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, size = 16.dp, tint = Primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Düzenle", color = Primary, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(24.dp))
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, size = 16.dp, tint = Error)
                    Spacer(Modifier.width(8.dp))
                    Text("Sil", color = Error, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("⚠️", style = MaterialTheme.typography.displaySmall)
            Text("Hata Oluştu", fontWeight = FontWeight.Bold, color = Error)
            Text(message, color = TextMutedAlt)
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Tekrar Dene") }
        }
    }
}




@Composable
fun EmptyListContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextMuted
        )
        Spacer(Modifier.height(16.dp))
        Text("Derslik bulunamadı", fontWeight = FontWeight.Bold, color = TextTitle)
        Text("Filtreleri veya aramayı temizlemeyi deneyin.", color = TextMutedAlt)
    }
}


@Composable
private fun ClassAlertDialog(
    className: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Sınıfı Sil", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = "$className adlı Sınıfı silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sil", color = Error, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = TextTitle)
            }
        },
        containerColor = Color.White,
        titleContentColor = Error,
        shape = RoundedCornerShape(14.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassRoomSheet(
    onDismiss: () -> Unit,
    onConfirm: (ClassRoom) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Sınıf") }

    var nameError by remember { mutableStateOf(false) }
    var capacityError by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val types = listOf("Sınıf", "Amfi", "Laboratuvar")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Yeni Derslik Ekle", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Derslik bilgilerini girin", style = MaterialTheme.typography.bodyMedium, color = TextMutedAlt)
                }
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(IconBgPrimary),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.School, null, tint = Primary) }
            }

            HorizontalDivider(color = DividerColor)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = it.isBlank() },
                label = { Text("Derslik Adı") },
                placeholder = { Text("Örn: A-101") },
                isError = nameError,
                supportingText = { if (nameError) Text("Ad boş olamaz", color = Error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary, focusedLabelColor = Primary, cursorColor = Primary
                ),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.MeetingRoom, null, tint = TextMutedAlt) }
            )

            OutlinedTextField(
                value = capacity,
                onValueChange = { if (it.all { char -> char.isDigit() }) { capacity = it; capacityError = it.isBlank() } },
                label = { Text("Kapasite") },
                placeholder = { Text("Örn: 50") },
                isError = capacityError,
                supportingText = { if (capacityError) Text("Kapasite boş olamaz", color = Error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary, focusedLabelColor = Primary, cursorColor = Primary
                ),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Groups, null, tint = TextMutedAlt) }
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tür") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    leadingIcon = { Icon(Icons.Default.Category, null, tint = TextMutedAlt) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary, focusedLabelColor = Primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    containerColor = Color.White
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Text("İptal")
                }
                Button(
                    onClick = {
                        nameError = name.isBlank()
                        capacityError = capacity.isBlank()
                        if (!nameError && !capacityError) {
                            onConfirm(ClassRoom(id = 0, name = name.trim(), capacity = capacity.toInt(), type = selectedType))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Ekle")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClassRoomSheet(
    classRoom: ClassRoom,
    onDismiss: () -> Unit,
    onConfirm: (ClassRoom) -> Unit
) {
    var name by remember { mutableStateOf(classRoom.name) }
    var capacity by remember { mutableStateOf(classRoom.capacity.toString()) }
    var selectedType by remember { mutableStateOf(classRoom.type) }

    var nameError by remember { mutableStateOf(false) }
    var capacityError by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val types = listOf("Sınıf", "Amfi", "Laboratuvar")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Dersliği Düzenle", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Bilgileri güncelleyin", style = MaterialTheme.typography.bodyMedium, color = TextMutedAlt)
                }
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(IconBgSecondary),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Edit, null, tint = Primary) }
            }

            HorizontalDivider(color = DividerColor)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = it.isBlank() },
                label = { Text("Derslik Adı") },
                isError = nameError,
                supportingText = { if (nameError) Text("Ad boş olamaz", color = Error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary, focusedLabelColor = Primary, cursorColor = Primary
                ),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.MeetingRoom, null, tint = TextMutedAlt) }
            )

            OutlinedTextField(
                value = capacity,
                onValueChange = { if (it.all { char -> char.isDigit() }) { capacity = it; capacityError = it.isBlank() } },
                label = { Text("Kapasite") },
                isError = capacityError,
                supportingText = { if (capacityError) Text("Kapasite boş olamaz", color = Error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary, focusedLabelColor = Primary, cursorColor = Primary
                ),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Groups, null, tint = TextMutedAlt) }
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tür") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    leadingIcon = { Icon(Icons.Default.Category, null, tint = TextMutedAlt) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary, focusedLabelColor = Primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    containerColor = Color.White
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Text("İptal")
                }
                Button(
                    onClick = {
                        nameError = name.isBlank()
                        capacityError = capacity.isBlank()
                        if (!nameError && !capacityError) {
                            onConfirm(classRoom.copy(name = name.trim(), capacity = capacity.toInt(), type = selectedType))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Save, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Güncelle")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Yardımcı Uzantılar (Icon boyutu için)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    size: Dp,
    tint: Color
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        tint = tint
    )
}
