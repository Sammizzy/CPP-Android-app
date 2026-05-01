package com.example.cppassignment2

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class AssetRepository(private val dao: AssetDao, private val networkHelper: NetworkHelper) {

    val allJobs: Flow<List<Job>> = dao.getAllJobs()

    fun getItemsForJob(jobId: String): Flow<List<InspectionItem>> = dao.getItemsForJob(jobId)

    suspend fun addInspection(item: InspectionItem) {
        dao.insertInspectionItem(item)
        // Only try to sync if network is actually available
        if (networkHelper.isNetworkAvailable()) {
            syncData()
        } else {
            Log.d("Sync", "Offline: Item ${item.id} saved locally only.")
        }
    }

    suspend fun syncData() {
        if (!networkHelper.isNetworkAvailable()) {
            Log.d("Sync", "Sync cancelled: No network connection.")
            return
        }

        val unsynced = dao.getUnsyncedItems()
        if (unsynced.isEmpty()) return

        Log.d("Sync", "Starting sync for ${unsynced.size} items...")
        
        try {
            unsynced.forEach { item ->
                // Simulate network latency
                delay(1000) 
                
                // Double check network before each item
                if (networkHelper.isNetworkAvailable()) {
                    dao.updateInspectionItem(item.copy(isSynced = true))
                    Log.d("Sync", "Successfully synced item: ${item.id}")
                }
            }
        } catch (e: Exception) {
            Log.e("Sync", "Sync failed: ${e.message}")
        }
    }
}
