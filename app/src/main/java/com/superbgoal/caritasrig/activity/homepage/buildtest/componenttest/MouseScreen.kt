package com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.model.component.Mouse
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

@Composable
fun MouseScreen(navController: NavController) {
    // Load mouse data
    val context = LocalContext.current
    val mice: List<Mouse> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.mouse // Ensure this JSON file exists in resources
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Main content with TopAppBar and Mouse List
        Column {
            TopAppBar(
                backgroundColor = Color.Transparent,
                contentColor = Color.White,
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = "Part Pick",
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Mice",
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        },
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Action for filter button
                        },
                        modifier = Modifier.padding(end = 20.dp, top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "Filter"
                        )
                    }
                }
            )

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                MouseList(mice, navController)
            }
        }
    }
}


@Composable
fun MouseList(mice: List<Mouse>, navController: NavController) {
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mice) { mouseItem ->
            // Track loading state for each mouse
            val isLoading = remember { mutableStateOf(false) }

            // Menggunakan ComponentCard untuk setiap mouse
            ComponentCard(
                title = mouseItem.name,
                details = "Type: ${mouseItem.name} | DPI: ${mouseItem.maxDpi} | Color: ${mouseItem.color}",
                context = context,
                component = mouseItem,
                isLoading = isLoading.value, // Pass loading state to card
                onAddClick = {
                    // Mulai proses loading ketika tombol Add ditekan
                    isLoading.value = true
                    Log.d("MouseActivity", "Selected Mouse: ${mouseItem.name}")

                    // Mendapatkan userId dan buildTitle
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()

                    // Simpan mouse jika buildTitle tersedia
                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "mouse", // Tipe komponen
                            componentData = mouseItem, // Data mouse
                            onSuccess = {
                                // Berhenti loading ketika sukses
                                isLoading.value = false
                                Log.d("MouseActivity", "Mouse ${mouseItem.name} saved successfully under build title: $title")

                            },
                            onFailure = { errorMessage ->
                                // Berhenti loading ketika gagal
                                isLoading.value = false
                                Log.e("MouseActivity", "Failed to store Mouse under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it } // Update loading state
                        )
                    } ?: run {
                        // Berhenti loading jika buildTitle null
                        isLoading.value = false
                        Log.e("MouseActivity", "Build title is null; unable to store Mouse.")
                    }
                }
            )
        }
    }
}