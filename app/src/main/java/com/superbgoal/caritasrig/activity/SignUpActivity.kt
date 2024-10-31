package com.superbgoal.caritasrig.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CaritasRigTheme {
                Scaffold {
                    SignUpScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstname by remember { mutableStateOf("") } // Field untuk Firstname
    var lastname by remember { mutableStateOf("") } // Field untuk Lastname
    var username by remember { mutableStateOf("") } // Field untuk Username
    var dateOfBirth by remember { mutableStateOf("") } // Field untuk Date of Birth
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.titleLarge)

        // Field untuk Firstname
        OutlinedTextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Field untuk Lastname
        OutlinedTextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Field untuk Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        // Field untuk Date of Birth
        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text("Date of Birth (DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Field untuk Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )


        // Field untuk Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        // Field untuk Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    signUpUser(email, password, firstname, lastname, username, dateOfBirth, confirmPassword, context)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }
    }
}

suspend fun signUpUser(
    email: String,
    password: String,
    firstname: String,
    lastname: String,
    username: String,
    dateOfBirth: String,
    confirmPassword: String,
    context: Context
) {
    val auth = FirebaseAuth.getInstance()

    // Validasi input password
    if (password != confirmPassword) {
        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        // Create account with email and password
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val userId = result.user?.uid

        Log.d("SignUpActivity", "User created successfully: $userId")

        if (userId != null) {
            // Save user details to Realtime Database
            val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
            val database: DatabaseReference = FirebaseDatabase.getInstance(databaseUrl).reference

            val userMap = mapOf(
                "firstName" to firstname,
                "lastName" to lastname,
                "username" to username,
                "email" to email,
                "dateOfBirth" to dateOfBirth
            )

            database.child("users").child(userId).setValue(userMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "Data berhasil disimpan.")
                    } else {
                        Log.e("Firebase", "Gagal menyimpan data: ${task.exception?.message}")
                    }
                }

            val intent = Intent(context, LoginActivity::class.java).apply {
                putExtra("email", email)
            }
            context.startActivity(intent)
            if (context is Activity) {
                context.finish()
            }
        } else {
            Log.e("SignUpActivity", "Failed to create account: User ID not found")
            Toast.makeText(context, "Failed to create account: User ID not found", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Log.e("SignUpActivity", "Failed to create account: ${e.message}")
        Toast.makeText(context, "Failed to create account: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}