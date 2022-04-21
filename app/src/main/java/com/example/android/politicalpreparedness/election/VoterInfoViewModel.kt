package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.DataRepository
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val election: Election,
    private val dataRepository: DataRepository
) : ViewModel() {

    companion object {
        private const val TAG = "projectlog"
    }

    private val _followedStatus = MutableLiveData<Boolean?>()
    val followedStatus: LiveData<Boolean?>
        get() = _followedStatus

    private val _voterInfoResponse = MutableLiveData<VoterInfoResponse?>()
    val voterInfoResponse: LiveData<VoterInfoResponse?>
        get() = _voterInfoResponse

    private val _currentElection = MutableLiveData<Election>()
    val currentElection: LiveData<Election>
        get() = _currentElection

    init {
        _currentElection.value = election
        Log.i(TAG, currentElection.value.toString())
        _followedStatus.value = currentElection.value?.followed
        getVoterInfoResponse()
    }

    private fun getVoterInfoResponse() {
        viewModelScope.launch {
            try {
                val address =
                    "${currentElection.value?.division?.state} ,${currentElection.value?.division?.country}"
                Log.i(TAG, address)
                val voterInfoResponseResult = CivicsApi.retrofitService.getVoterInfo(
                    address,
                    currentElection.value?.id!!.toLong()
                )
                _voterInfoResponse.value = voterInfoResponseResult
                Log.i(TAG, voterInfoResponseResult.toString())
                Log.i(TAG, voterInfoResponseResult.election.toString())
                Log.i(TAG, voterInfoResponseResult.state.toString())
            } catch (e: Exception) {
                _voterInfoResponse.value = null
                Log.i(TAG, "${e.printStackTrace()}")
            }
        }
    }

    fun updateElection() {
        viewModelScope.launch {
            try {
                dataRepository.updateElection(currentElection.value?.id!!, followedStatus.value!!)
            } catch (e: Exception) {
                Log.i(TAG, "${e.printStackTrace()}")
            }
        }
    }

    fun updateFollowedStatus(status: Boolean) {
        _followedStatus.value = status
    }

}