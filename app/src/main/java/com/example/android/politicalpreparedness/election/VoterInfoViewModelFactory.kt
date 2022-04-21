package com.example.android.politicalpreparedness.election

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.android.politicalpreparedness.data.DataRepository
import com.example.android.politicalpreparedness.network.models.Election

@Suppress("UNCHECKED_CAST")
class VoterInfoViewModelFactory(
    private val election: Election,
    private val dataRepository: DataRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VoterInfoViewModel(election, dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
