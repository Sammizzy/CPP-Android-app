package com.example.cppassignment2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val status: String, // "Pending", "Completed", "Synced"
    val lastUpdated: Long
)

@Entity(tableName = "inspection_items")
data class InspectionItem(
    @PrimaryKey val id: String,
    val jobId: String,
    val title: String,
    val result: String?,
    val isSynced: Boolean = false
)
