package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.DataRepository

@Suppress("UNCHECKED_CAST")
class RepresentativeViewModelFactory(private val dataRepository: DataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (RepresentativeViewModel(dataRepository) as T)
}