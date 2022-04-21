package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.DataRepository

@Suppress("UNCHECKED_CAST")
class ElectionsViewModelFactory(private val dataRepository: DataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (ElectionsViewModel(dataRepository) as T)
}