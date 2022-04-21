package com.example.android.politicalpreparedness.data

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Error
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DataRepository(
    private val electionDao: ElectionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    companion object {
        private const val TAG = "projectlog"
    }

    val localDate = LocalDate.now(ZoneId.systemDefault())
    val currentformattedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

    fun observeFollowedElections(): LiveData<Result<List<Election>>> {
        return Transformations.map(electionDao.observeFollowedElections(true)) {
            Result.Success(it)
        }
    }

    fun observeElections(): LiveData<Result<List<Election>>> {
        return Transformations.map(electionDao.observeElections()) {
            Result.Success(it)
        }
    }

    fun observeElectionById(electionId: Int): LiveData<Result<Election>> {
        return Transformations.map(electionDao.observeElectionById(electionId)) {
            Result.Success(it)
        }
    }

    suspend fun deletePastElections() {
        withContext(ioDispatcher) {
            Log.i(TAG, currentformattedDate.toString())
            electionDao.deletePastElections(currentformattedDate)
        }
    }

    suspend fun insertAllElections(elections: List<Election>) {
        withContext(ioDispatcher) {
            electionDao.insertAllElections(elections)
        }
    }

    suspend fun updateElection(electionId: Int, followed: Boolean) {
        withContext(ioDispatcher) {
            electionDao.updateElection(electionId, followed)
        }
    }

    suspend fun refreshElectionsFromNetwork() {
        withContext(ioDispatcher) {
            val electionResponse = CivicsApi.retrofitService.getElections()
            val elections = electionResponse.elections
            insertAllElections(elections)
            deletePastElections()
        }
    }

    suspend fun refreshRepFromNetwork(address: Address): Result<RepresentativeResponse> {
        return withContext(ioDispatcher) {
            try {
                val repResponse =
                    CivicsApi.retrofitService.getRepresentatives(address.toFormattedString())
                Result.Success(repResponse)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getElections(): Result<List<Election>> {
        return withContext(ioDispatcher) {
            try {
                Result.Success(electionDao.getElections())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getElectionById(electionId: Int): Result<Election> {
        return withContext(ioDispatcher) {
            try {
                val election = electionDao.getElectionById(electionId)
                if (election != null) {
                    Result.Success(election)
                } else {
                    Result.Error(Exception("Task not found!"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}