package com.example.cppassignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cppassignment2.ui.theme.CPPAssignment2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPPAssignment2Theme {
                AssetGuardApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetGuardApp(viewModel: AssetViewModel = viewModel()) {
    val jobs by viewModel.jobs.collectAsState()
    val unsyncedCount by viewModel.unsyncedItemsCount.collectAsState()
    var selectedJobId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AssetGuard") },
                actions = {
                    if (unsyncedCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("$unsyncedCount", color = Color.White)
                        }
                    }
                    IconButton(onClick = { viewModel.seedData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Data")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Persistent Sync Banner
            if (unsyncedCount > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Waiting to sync $unsyncedCount items...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (selectedJobId == null) {
                JobList(jobs, onJobClick = { selectedJobId = it.id }, onSeed = { viewModel.seedData() })
            } else {
                val jobTitle = jobs.find { it.id == selectedJobId }?.title ?: "Inspection"
                InspectionList(
                    jobId = selectedJobId!!,
                    jobTitle = jobTitle,
                    viewModel = viewModel,
                    onBack = { selectedJobId = null }
                )
            }
        }
    }
}

@Composable
fun JobList(jobs: List<Job>, onJobClick: (Job) -> Unit, onSeed: () -> Unit) {
    if (jobs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No inspection jobs found.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onSeed) {
                    Text("Seed Sample Data")
                }
            }
        }
    } else {
        LazyColumn {
            items(jobs) { job ->
                ListItem(
                    headlineContent = { Text(job.title) },
                    supportingContent = { Text(job.description) },
                    trailingContent = { 
                        Text(
                            text = job.status,
                            color = if (job.status == "Synced") Color(0xFF4CAF50) else Color.Gray
                        ) 
                    },
                    modifier = Modifier
                        .clickable { onJobClick(job) }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun InspectionList(jobId: String, jobTitle: String, viewModel: AssetViewModel, onBack: () -> Unit) {
    val items by viewModel.getItemsForJob(jobId).collectAsState(initial = emptyList())
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBack) { Text("Back to Jobs") }
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Inspections for $jobTitle", style = MaterialTheme.typography.headlineSmall)
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Result: ${item.result ?: "Pending"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (item.isSynced) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Synced",
                            tint = Color(0xFF4CAF50)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Pending Sync",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "Pending Sync",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                HorizontalDivider()
            }
        }
        
        Button(
            onClick = { viewModel.addInspection(jobId, "Structural Check", "Pass") },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log New Inspection (Offline)")
        }
    }
}
