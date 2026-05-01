package com.example.cppassignment2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class AssetViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AssetDatabase::class.java, "asset-db"
    ).build()

    private val networkHelper = NetworkHelper(application)
    private val repository = AssetRepository(db.assetDao(), networkHelper)

    val jobs: StateFlow<List<Job>> = repository.allJobs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val unsyncedItemsCount: StateFlow<Int> = db.assetDao().getUnsyncedItemsFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    ).let { flow ->
        val countFlow = kotlinx.coroutines.flow.MutableStateFlow(0)
        viewModelScope.launch {
            db.assetDao().getUnsyncedItemsFlow().collect {
                countFlow.value = it.size
            }
        }
        countFlow
    }

    init {
        seedData()
    }

    fun seedData() {
        viewModelScope.launch {
            val currentJobs = db.assetDao().getAllJobsOnce()
            if (currentJobs.isEmpty()) {
                db.assetDao().insertJob(Job("1", "Tower Site A", "Safety inspection of structural bolts.", "Pending", System.currentTimeMillis()))
                db.assetDao().insertJob(Job("2", "Substation 4", "Oil level and temperature check.", "Pending", System.currentTimeMillis()))
            }
            repository.syncData()
        }
    }

    fun addInspection(jobId: String, title: String, result: String) {
        viewModelScope.launch {
            val newItem = InspectionItem(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                title = title,
                result = result,
                isSynced = false
            )
            repository.addInspection(newItem)
        }
    }
    
    fun getItemsForJob(jobId: String) = repository.getItemsForJob(jobId)
}
