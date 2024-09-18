package com.example.istandroid

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun AddJobScreen(navController: NavController, jobViewModel: JobViewModel = viewModel()) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    var userRole by remember { mutableStateOf<String?>(null) }
    var jobTitle by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var jobDescription by remember { mutableStateOf("") }
    var jobLocation by remember { mutableStateOf("") }
    var jobSalary by remember { mutableStateOf("") }
    var applyLink by remember { mutableStateOf("") }
    var qualifications by remember { mutableStateOf("") }
    var isFullTime by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingRole by remember { mutableStateOf(true) }

    // Check the user's role if they are an admin
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    userRole = document.getString("role")
                    isLoadingRole = false
                }
                .addOnFailureListener {
                    userRole = null
                    isLoadingRole = false
                }
        }
    }

    if (isLoadingRole) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // Subtle loading for role checking
        }
    } else {
        if (userRole == "admin") {
            Crossfade(targetState = isLoading) { loading ->
                if (loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(modifier = Modifier.size(50.dp)) // Subtle, centered loading
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Posting job...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    JobForm(
                        navController = navController,
                        jobViewModel = jobViewModel,
                        jobTitle = jobTitle,
                        companyName = companyName,
                        jobDescription = jobDescription,
                        jobLocation = jobLocation,
                        jobSalary = jobSalary,
                        applyLink = applyLink,
                        qualifications = qualifications,
                        isFullTime = isFullTime,
                        onJobTitleChange = { jobTitle = it },
                        onCompanyNameChange = { companyName = it },
                        onJobDescriptionChange = { jobDescription = it },
                        onJobLocationChange = { jobLocation = it },
                        onJobSalaryChange = { jobSalary = it },
                        onApplyLinkChange = { applyLink = it },
                        onQualificationsChange = { qualifications = it },
                        onFullTimeChange = { isFullTime = it },
                        onSubmit = {
                            if (jobTitle.isNotEmpty() && companyName.isNotEmpty() && jobDescription.isNotEmpty()) {
                                isLoading = true

                                val jobData = JobData(
                                    jobID = db.collection("jobs").document().id,
                                    title = jobTitle,
                                    companyName = companyName,
                                    location = jobLocation,
                                    description = jobDescription,
                                    salary = jobSalary,
                                    applyLink = applyLink,
                                    qualifications = qualifications,
                                    isFullTime = isFullTime,
                                    postDate = System.currentTimeMillis().toString()
                                )

                                jobViewModel.saveJob(
                                    jobData = jobData,
                                    context = navController.context
                                )

                                navController.navigate(Screen.JobScreen.route)
                            }
                        }
                    )
                }
            }
        } else {
            LaunchedEffect(Unit) {
                navController.navigate("dashboard_screen")
            }
        }
    }
}

@Composable
fun LaunchedEffect(unit: String?, content: () -> Unit) {

}

@Composable
fun <JobViewModel> JobForm(
    navController: NavController,
    jobViewModel: JobViewModel,
    jobTitle: String,
    companyName: String,
    jobDescription: String,
    jobLocation: String,
    jobSalary: String,
    applyLink: String,
    qualifications: String,
    isFullTime: Boolean,
    onJobTitleChange: (String) -> Unit,
    onCompanyNameChange: (String) -> Unit,
    onJobDescriptionChange: (String) -> Unit,
    onJobLocationChange: (String) -> Unit,
    onJobSalaryChange: (String) -> Unit,
    onApplyLinkChange: (String) -> Unit,
    onQualificationsChange: (String) -> Unit,
    onFullTimeChange: (Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Post a New Job", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = jobTitle,
            onValueChange = onJobTitleChange,
            label = { Text("Job Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = companyName,
            onValueChange = onCompanyNameChange,
            label = { Text("Company Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = jobDescription,
            onValueChange = onJobDescriptionChange,
            label = { Text("Job Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = jobLocation,
            onValueChange = onJobLocationChange,
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = jobSalary,
            onValueChange = onJobSalaryChange,
            label = { Text("Salary (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = applyLink,
            onValueChange = onApplyLinkChange,
            label = { Text("Apply Link") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = qualifications,
            onValueChange = onQualificationsChange,
            label = { Text("Qualifications") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isFullTime,
                onCheckedChange = onFullTimeChange
            )
            Text("Full Time")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Post Job")
        }
    }
}