package com.serveterdogan.genetikalgoritmamobil.feature.course.faculty

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serveterdogan.genetikalgoritmamobil.domain.model.Faculty
import com.serveterdogan.genetikalgoritmamobil.domain.repository.FacultyRepository
import com.serveterdogan.genetikalgoritmamobil.uıState.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacultyViewModel @Inject constructor(
    private  val repo : FacultyRepository
) : ViewModel() {


    /*
      StateFlow oluşturacağım repositorden gelen veri sonucunu uı dinamik olarak vermek bağlamak için kullanacağız
       ui state mamangment için UiState kullanacağım böylleikle ui hangi durumda  olduğunu anlayabileceğiz
     */

    private val _state = MutableStateFlow<UiState<List<Faculty>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Faculty>>> = _state


    init {
        // FacultyViewModel counstractırı çağrıldığı zaman ilk burası çalışacak ilk verileri yükleyecek
        loadFaculty()
    }


    fun loadFaculty() {
        // coroutine başlatıyorum böylelikle fun asenkron olarak çalışabilecek uzun sürecek işlemleri witchContext ile başka threade atabilriiz
        viewModelScope.launch {
            // yüklenmeden önce uı da loading gösterelim
            _state.value = UiState.Loading
            val result =
                repo.getFaculties() // suspend çağrısı yapıyoruz eğer işlem çok uzun sürerse coroutine askıya alır ve böylelikle thread boşa çıkacak

            result.fold(
                onSuccess = { faculties ->
                    _state.value = UiState.Success(faculties)
                },
                onFailure = { error ->
                    _state.value = UiState.Error(error.message ?: "Hata")
                }

            )
        }

    }

    /*
    delete , update , add işlemlerinde bir daha backend istek atıp uzun sürebilecek işlmei beklmeye gerek
     yoksa direkt mevcut viewmodel beleğindeki veri listesini günceleyebiliriz böylelikle performans kaybı da olmamış olur
     */

    fun deleteFaculty(deleteId : Int){
            viewModelScope.launch {
              //  _state.value = UiState.Loading

                repo.deleteFaculty(id = deleteId)
                    .onSuccess {
                        val current = (_state.value as? UiState.Success)?.data ?:emptyList()
                        val updatedList = current.filter { it.id != deleteId}
                        _state.value = UiState.Success(updatedList)
                    }
                    .onFailure {error->
                        _state.value = UiState.Error(error.message ?: "Faculteye silerken hata oluştu")
                    }

            }
    }

    fun addFaculty(faculty: Faculty){
        viewModelScope.launch {
          //  _state.value = UiState.Loading

            repo.addFaculty(faculty = faculty)
                .onSuccess { newFaculty ->
                    val current = (_state.value as? UiState.Success)?.data ?:emptyList()
                    val updateList = current + newFaculty
                    _state.value = UiState.Success(updateList)
                }
                .onFailure { error->
                    _state.value = UiState.Error(error.message ?: "Faculteye eklerken hata oluştu")
                }
        }
    }

    fun updateFaculty(faculty: Faculty, id: Int) {
        viewModelScope.launch {
            repo.updateFaculty(id = id, faculty = faculty)
                .onSuccess {
                    val current = (_state.value as? UiState.Success)?.data ?: emptyList()
                    val updatedList = current.map { if (it.id == faculty.id) faculty else it }
                    _state.value = UiState.Success(updatedList) // UI hemen güncellenir
                }
                .onFailure { error ->
                    _state.value = UiState.Error(error.message ?: "Faculteye güncellerken hata oluştu")
                }
        }
    }


}