package com.example.istandroid.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istandroid.R
import com.example.istandroid.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavController) {
    // Initialize Firebase Auth
    auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showPasswordToggle by remember { mutableStateOf(false) }
    val errorMessage by remember { mutableStateOf<String?>(null) }
    val successMessage by remember { mutableStateOf<String?>(null) }

    val visualTransformation = if (passwordVisible) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

//                Image(
//                    modifier = Modifier
//                        .size(100.dp)
//                        .clip(RoundedCornerShape(56.dp)),
//                    painter = painterResource(R.drawable.project_logo),
//                    contentDescription = "Login"
//                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = visualTransformation,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Checkbox for toggling password visibility
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = showPasswordToggle,
                        onCheckedChange = { isChecked ->
                            showPasswordToggle = isChecked
                            passwordVisible = isChecked
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Show Password",color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    text = "Forgot Password?",
//                    color = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.clickable {
//                        navController.navigate(Screens.ForgotPasswordScreen.route)
//                    }
//                )


                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            signIn(email, password, auth, navController,
                                { user ->
//                                    navController.navigate(Screens.DashboardScreen.route) // Navigate to Dashboard on success
                                },
                                { error ->
                                })
                        } else {
                            Toast.makeText(navController.context, "Please enter both email and password", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log In")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Don't have an account??"
                )
                Button(onClick = { navController.navigate(Screens.RegisterScreen.route) }) {
                    Text(text = "Register")
                }
                // Display error or success message
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                successMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

private fun signIn(
    email: String,
    password: String,
    auth: FirebaseAuth,
    navController: NavController,
    onSuccess: (FirebaseUser?) -> Unit,
    onFailure: (String) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null && user.isEmailVerified) {
                    Toast.makeText(navController.context, "Welcome ${user.email}", Toast.LENGTH_LONG).show()
                    onSuccess(user)
                } else {
                    auth.signOut()
                    val errorMessage = "Email not verified. Please verify your email."
                    Toast.makeText(navController.context, errorMessage, Toast.LENGTH_LONG).show()
                    onFailure(errorMessage)
                }
            } else {
                val error = task.exception?.message ?: "Sign in failed"
                Toast.makeText(navController.context, error, Toast.LENGTH_LONG).show()
                onFailure(error)
            }
        }
}