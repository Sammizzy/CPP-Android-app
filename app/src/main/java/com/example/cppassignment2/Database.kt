package com.example.cppassignment2

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM jobs")
    fun getAllJobs(): Flow<List<Job>>

    @Query("SELECT * FROM jobs")
    suspend fun getAllJobsOnce(): List<Job>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: Job)

    @Query("SELECT * FROM inspection_items WHERE jobId = :jobId")
    fun getItemsForJob(jobId: String): Flow<List<InspectionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspectionItem(item: InspectionItem)

    @Query("SELECT * FROM inspection_items WHERE isSynced = 0")
    fun getUnsyncedItemsFlow(): Flow<List<InspectionItem>>

    @Query("SELECT * FROM inspection_items WHERE isSynced = 0")
    suspend fun getUnsyncedItems(): List<InspectionItem>

    @Update
    suspend fun updateInspectionItem(item: InspectionItem)
}

@Database(entities = [Job::class, InspectionItem::class], version = 1)
abstract class AssetDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
}
