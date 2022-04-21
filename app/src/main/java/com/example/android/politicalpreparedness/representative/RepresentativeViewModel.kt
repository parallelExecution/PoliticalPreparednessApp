package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.DataRepository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel(private val dataRepository: DataRepository) : ViewModel() {

    companion object {
        private const val TAG = "projectlog"
    }

    private val _representatives = MutableLiveData<List<Representative>?>()
    val representatives: LiveData<List<Representative>?>
        get() = _representatives

    private val _address = MutableLiveData<Address?>()
    val address: LiveData<Address?>
        get() = _address

    fun refreshRepFromNetwork(address: Address) {
        viewModelScope.launch {
            val result = dataRepository.refreshRepFromNetwork(address)
            if (result is Result.Success) {
                val (offices, officials) = result.data
                _representatives.value =
                    offices.flatMap { office -> office.getRepresentatives(officials) }
            } else if (result is Result.Error) {
                Log.i(TAG, result.exception.printStackTrace().toString())
            }
        }
    }

    fun updateAddress(currentAddress: Address) {
        _address.value = currentAddress
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */
}
