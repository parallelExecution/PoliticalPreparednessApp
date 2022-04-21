package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.DataRepository
import com.example.android.politicalpreparedness.data.ElectionDao
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class ElectionsViewModel(private val dataRepository: DataRepository) : ViewModel() {

    companion object {
        private const val TAG = "projectlog"
    }

    val observedElections = dataRepository.observeElections()
    val observedFollowedElections = dataRepository.observeFollowedElections()

    init {
        refreshElectionsFromNetwork()
    }

    private fun refreshElectionsFromNetwork() {
        viewModelScope.launch {
            try {
                dataRepository.refreshElectionsFromNetwork()
            } catch (e: Exception) {
                Log.i(TAG, "${e.printStackTrace()}")
            }
        }
    }

}