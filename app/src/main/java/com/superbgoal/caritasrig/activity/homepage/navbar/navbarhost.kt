package com.superbgoal.caritasrig.activity.homepage.navbar

import BuildViewModel
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.AsyncImage
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.buildtest.BuildListScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.BuildScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.CasingScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.CpuCoolerScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.CpuScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.HeadphoneScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.InternalHardDriveScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.KeyboardScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.MotherboardScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.MouseScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.PowerSupplyScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.VideoCardScreen
import com.superbgoal.caritasrig.activity.homepage.home.HomeScreen
import com.superbgoal.caritasrig.activity.homepage.home.HomeViewModel
import com.superbgoal.caritasrig.activity.homepage.profileicon.AboutUsScreen
import com.superbgoal.caritasrig.activity.homepage.profileicon.profilesettings.ProfileSettingsViewModel
import com.superbgoal.caritasrig.activity.homepage.screentest.ProfileSettingsScreen
import com.superbgoal.caritasrig.activity.homepage.screentest.SettingsScreen
import com.superbgoal.caritasrig.data.model.User

@Composable
fun NavbarHost(
    homeViewModel: HomeViewModel = viewModel(),
    profileViewModel: ProfileSettingsViewModel = viewModel(),
    buildViewModel: BuildViewModel = viewModel(),
    appController : NavController
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            val currentBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry.value?.destination?.route
            val isProfileScreen = currentRoute?.startsWith("profile") == true

            val specificRoutes = listOf("settings", "about_us", "settings_profile")
            val isSpecificRoute = specificRoutes.contains(currentRoute)

            val title = when (currentRoute) {
                "home" -> "Home"
                "settings" -> "Settings"
                "about_us" -> "About Us"
                "settings_profile" -> stringResource(id = R.string.profile_settings)
                "trending" -> "Trending"
                "build" -> "Build"
                "benchmark" -> "Benchmark"
                "favorite" -> "Favorite Component"
                else -> "CaritasRig"
            }

            AppTopBar(
                navigateToProfile = { user ->
                    navController.navigate("profile/${user?.username ?: "unknown"}") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                navigateToSettings = {
                    navController.navigate("settings") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                isProfileScreen = isProfileScreen,
                title = title,
                isSpecificRoute = isSpecificRoute,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                        1 -> navController.navigate("trending") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                        2 -> navController.navigate("build") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                        3 -> navController.navigate("benchmark") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                        4 -> navController.navigate("favorite") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(viewModel = homeViewModel)
            }
            composable("profile/{username}") {
                ProfileScreen(homeViewModel = homeViewModel)
            }
            composable("settings") {
                SettingsScreen(navController,appController)
            }
            composable("about_us") {
                AboutUsScreen()
            }
            composable("settings_profile") {
                ProfileSettingsScreen(profileViewModel, homeViewModel)
            }
            composable("trending") {
                Text(text = "Trending")
            }
            composable("build") {
                BuildListScreen(navController)
            }
            composable("benchmark") {
                Text(text = "Benchmark")
            }
            composable("favorite") {
                Text(text = "Favorite")
            }
            composable(
                route = "build_details/{title}",
                arguments = listOf(navArgument("title") { type = NavType.StringType })
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                BuildScreen(title = title, buildViewModel, navController)
            }
            composable("cpu_screen") { CpuScreen(navController) }
            composable("casing_screen") { CasingScreen(navController) }
            composable("cpu_cooler_screen") { CpuCoolerScreen(navController) }
            composable("gpu_screen") { VideoCardScreen(navController) }
            composable("motherboard_screen") { MotherboardScreen(navController) }
            composable("internal_hard_drive_screen") { InternalHardDriveScreen(navController) }
            composable("power_supply_screen") { PowerSupplyScreen(navController) }
            composable("headphone_screen") { HeadphoneScreen(navController) }
            composable("keyboard_screen") { KeyboardScreen(navController) }
            composable("mouse_screen") { MouseScreen(navController) }
        }
    }
}




@Composable
fun AppTopBar(
    homeViewModel: HomeViewModel = viewModel(),
    navigateToProfile: (User?) -> Unit,
    navigateToSettings: () -> Unit,
    onBackClick: () -> Unit = {},
    isProfileScreen: Boolean = false,
    isSpecificRoute: Boolean = false,
    title: String
) {
    val user by homeViewModel.user.collectAsState(initial = null)

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tampilkan tombol back jika berada di specific route atau profile screen
                if (isSpecificRoute || isProfileScreen) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Text(text = title, fontSize = 20.sp)
            }
        },
        actions = {
            if (isSpecificRoute) {
                // Tidak menampilkan aksi tambahan pada specific route (bisa diubah jika diperlukan)
            } else if (isProfileScreen) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon Settings
                    IconButton(onClick = { navigateToSettings() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                // Jika bukan di halaman profil, tampilkan icon profil
                IconButton(onClick = { navigateToProfile(user) }) {
                    if (user?.profileImageUrl != null) {
                        AsyncImage(
                            model = user?.profileImageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape),
                            placeholder = painterResource(id = R.drawable.baseline_person_24),
                            error = painterResource(id = R.drawable.baseline_person_24)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Default Profile Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        elevation = 4.dp
    )
}

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Trending,
        NavigationItem.Build,
        NavigationItem.Benchmark,
        NavigationItem.Favorite
    )

    BottomAppBar(
        cutoutShape = CircleShape,
        elevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            val isMiddle = index == 2
            val isSelected = selectedItem == index

            if (isMiddle) {
                FloatingActionButton(
                    onClick = { onItemSelected(index) },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                }
            } else {
                // Menambahkan animasi untuk transisi warna ikon
                val animatedIconTint = animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colors.primary else Color.Gray
                )
                val animatedSize = animateDpAsState(
                    targetValue = if (isSelected) 30.dp else 24.dp
                )

                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = animatedIconTint.value, // Animasi warna
                            modifier = Modifier.size(animatedSize.value) // Animasi ukuran
                        )
                    },
                    selected = isSelected,
                    onClick = { onItemSelected(index) },
                    label = {
                        Text(
                            text = item.title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colors.primary else Color.Gray
                        )
                    },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = Color.Gray
                )
            }
        }
    }
}


sealed class NavigationItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : NavigationItem("Home", Icons.Default.Home) // Pastikan ikon ini valid
    object Trending : NavigationItem("Trending", Icons.Filled.TrendingUp) // Ubah ke Icons.Filled agar lebih stabil
    object Build : NavigationItem("Build", Icons.Default.Build)
    object Benchmark : NavigationItem("Benchmark", Icons.Default.BarChart)
    object Favorite : NavigationItem("Favorite", Icons.Default.Favorite)
}


@Composable
fun ProfileScreen(homeViewModel: HomeViewModel) {
    val user by homeViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadUserData("currentUserId")
    }

    val currentUser = user // Salin nilai user ke variabel lokal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        if (currentUser?.profileImageUrl != null) {
            AsyncImage(
                model = currentUser.profileImageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colors.primary, CircleShape),
                placeholder = painterResource(id = R.drawable.baseline_person_24),
                error = painterResource(id = R.drawable.baseline_person_24)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Default Profile Icon",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colors.primary, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${currentUser?.firstName ?: "First Name"} ${currentUser?.lastName ?: "Last Name"}",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "@${currentUser?.username ?: "username"}",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

    }
}

