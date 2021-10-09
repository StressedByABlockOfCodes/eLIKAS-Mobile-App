package com.example.elikas.viewmodel

import androidx.lifecycle.*
import com.example.elikas.data.DisasterResponse
import com.example.elikas.data.DisasterResponseRepository
import kotlinx.coroutines.launch

class DisasterResponseViewModel(private val disasterResponseRepo: DisasterResponseRepository) : ViewModel() {

    val allDisasterResponses: LiveData<List<DisasterResponse>> = disasterResponseRepo.allDisasterResponses.asLiveData()

    fun getDisasterResponseByID(id: Int) = disasterResponseRepo.getDisasterResponseByID(id)

    fun insertAll(disasterResponses: List<DisasterResponse>) = viewModelScope.launch {
        disasterResponseRepo.insertAll(disasterResponses)
    }

    fun removeAll() = viewModelScope.launch {
        disasterResponseRepo.removeAll()
    }
}

class DisasterResponseViewModelFactory(private val repository: DisasterResponseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DisasterResponseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DisasterResponseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}