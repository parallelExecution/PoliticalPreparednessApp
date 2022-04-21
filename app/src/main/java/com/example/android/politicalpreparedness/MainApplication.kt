package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.data.DataRepository

class MainApplication : Application() {

    val dataRepository: DataRepository
        get() = ServiceLocator.provideDataRepository(this)
}