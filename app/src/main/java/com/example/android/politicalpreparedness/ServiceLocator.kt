package com.example.android.politicalpreparedness

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.android.politicalpreparedness.data.DataRepository
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.election.VoterInfoViewModelFactory

object ServiceLocator {

    private var database: ElectionDatabase? = null

    @Volatile
    var dataRepository: DataRepository? = null

    private val lock = Any()

    fun provideDataRepository(context: Context): DataRepository {
        synchronized(this) {
            return dataRepository ?: createDataRepository(context)
        }
    }

    private fun createDataRepository(context: Context): DataRepository {
        val database = database ?: createDataBase(context)
        val newRepo = DataRepository(database.electionDao)
        dataRepository = newRepo
        return newRepo
    }

    private fun createDataBase(context: Context): ElectionDatabase {
        val result = ElectionDatabase.getInstance(context)
        database = result
        return result
    }
}