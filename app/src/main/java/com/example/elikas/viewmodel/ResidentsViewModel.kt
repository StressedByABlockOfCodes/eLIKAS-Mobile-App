package com.example.elikas.viewmodel

import androidx.lifecycle.*
import com.example.elikas.data.ResidentRepository
import com.example.elikas.data.Resident
import kotlinx.coroutines.launch

class ResidentsViewModel(private val residentRepository: ResidentRepository) : ViewModel() {

    val allResidents: LiveData<List<Resident>> = residentRepository.allResidents.asLiveData()

    fun getResidentsByFamCode(fam_code: String) = residentRepository.getResidentsByFamCode(fam_code)

    fun insertAll(allresident: List<Resident>) = viewModelScope.launch {
        residentRepository.insertAll(allresident)
    }

    fun removeAll() = viewModelScope.launch {
        residentRepository.removeAll()
    }
}

class ResidentViewModelFactory(private val repository: ResidentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResidentsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResidentsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}