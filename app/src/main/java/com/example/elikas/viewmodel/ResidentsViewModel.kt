package com.example.elikas.viewmodel

import androidx.lifecycle.*
import com.example.elikas.data.ResidentRepository
import com.example.elikas.data.Resident
import kotlinx.coroutines.launch

class ResidentsViewModel(private val residentRepository: ResidentRepository) : ViewModel() {

    val allResidents: LiveData<List<Resident>> = residentRepository.allResidents.asLiveData()

    fun getResidentsByFamCode(fam_code: String): LiveData<List<Resident>>
        = residentRepository.getResidentsByFamCode(fam_code).asLiveData()

    fun getEvacuees(): LiveData<List<Resident>>
            = residentRepository.getEvacuees().asLiveData()

    fun getNonEvacuees(): LiveData<List<Resident>>
            = residentRepository.getNonEvacuees().asLiveData()

    fun getFamilyHeadsEvacuees(): LiveData<List<Resident>>
            =  residentRepository.getFamilyHeadsEvacuees().asLiveData()

    fun changeToEvacuee(fam_code: String) = viewModelScope.launch {
        residentRepository.changeToEvacuee(fam_code)
    }

    fun changeToNonEvacuee(fam_code: String) = viewModelScope.launch {
        residentRepository.changeToNonEvacuee(fam_code)
    }

    fun changeToEvacuees(allResident: List<Resident>) = viewModelScope.launch {
        residentRepository.changeToEvacuees(allResident)
    }

    fun changeToNonEvacuees(allResident: List<Resident>) = viewModelScope.launch {
        residentRepository.changeToNonEvacuees(allResident)
    }

    fun insertAll(allResident: List<Resident>) = viewModelScope.launch {
        residentRepository.insertAll(allResident)
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