package com.serveterdogan.genetikalgoritmamobil.feature.course.classroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serveterdogan.genetikalgoritmamobil.domain.model.ClassRoom
import com.serveterdogan.genetikalgoritmamobil.domain.repository.ClassRoomsRepository
import com.serveterdogan.genetikalgoritmamobil.uiState.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassRoomViewModel @Inject constructor(
    private val repo : ClassRoomsRepository
) : ViewModel(){


    private val _allClassRooms = MutableStateFlow<List<ClassRoom>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedType = MutableStateFlow("Tümü")
    val selectedType: StateFlow<String> = _selectedType

    private val _state = MutableStateFlow<UiState<List<ClassRoom>>>(UiState.Loading)
    val state: StateFlow<UiState<List<ClassRoom>>> = _state

    // snackbar da ve toas mesajalrda gösterceeğimiz mesajlar
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterClassRooms()
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = type
        filterClassRooms()
    }


    fun clearUserMessage() { _userMessage.value = null }

    private fun filterClassRooms() {
        val currentList = _allClassRooms.value
        val query = _searchQuery.value.lowercase()
        val type = _selectedType.value

        val filteredList = currentList.filter { room ->
            val matchesQuery = room.name.lowercase().contains(query)
            val matchesType = type == "Tümü" || room.type == type
            matchesQuery && matchesType
        }
        _state.value = UiState.Success(filteredList)
    }


    init {
        loadClass()
    }


    fun loadClass(){
        viewModelScope.launch {
            _state.value = UiState.Loading
             repo.getClassRooms().fold(
                onSuccess = { classrooms->
                    _allClassRooms.value = classrooms
                    filterClassRooms()
                },
                onFailure = {error->
                    _state.value = UiState.Error(message = error.message ?:"Sınıflar Yüklenirken Hata oluştu")
                }
            )
        }
    }


    fun addClassRoom(classRoom: ClassRoom){
        viewModelScope.launch {
            repo.addClassRoom(classRoom).fold(
                onSuccess = { (newRoom , message)->
                    // backenden gelen newRoom ekliyoruz
                    _allClassRooms.value = _allClassRooms.value + newRoom
                    filterClassRooms() // listeyi yenile
                    _userMessage.value =  message
                },
                onFailure = {error->
                    _userMessage.value = error.message ?: "Ekleme başarısız"
                }
            )
        }
    }


    fun updateClass(id: Int , classRoom: ClassRoom){
        viewModelScope.launch {
            repo.updateClassRoom(id = id , classRoom = classRoom).fold(
                onSuccess = {successMsg->
                    _allClassRooms.value = _allClassRooms.value.map {
                        if (it.id == id) classRoom else it
                    }
                    filterClassRooms()
                    _userMessage.value = successMsg
                },
                onFailure = {error->
                    _userMessage.value = error.message ?: "Güncelleme başarısız"
                }
            )
        }
    }


    fun deleteClass(id : Int){
        viewModelScope.launch {
            repo.deleteClassRoom(id = id).fold(
                onSuccess = {successMsg->
                    // Listeyi günceliyoruz
                   _allClassRooms.value = _allClassRooms.value.filter { it.id != id }
                    filterClassRooms()
                    _userMessage.value = successMsg
                },
                onFailure = {error->
                    _userMessage.value = error.message ?: "Silme işlemi başarısız"
                }
            )
        }
    }

}