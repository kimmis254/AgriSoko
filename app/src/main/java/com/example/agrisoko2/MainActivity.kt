package com.example.agrisoko2

import FarmerHomeScreen
import RegisterScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agrisoko2.screens.CustomerHomeScreen
import com.example.agrisoko2.screens.LoginScreen
import com.example.agrisoko2.screens.SplashScreen
import com.example.agrisoko2.ui.theme.AgriSoko2Theme
import com.example.agrisoko2.viewmodels.AuthenticationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        WindowCompat.setDecorFitsSystemWindows(window, false )

        setContent {
            AgriSoko2Theme {
                val navController = rememberNavController()

                SetupNavGraph(navController = navController)

                CheckUserStatus(navController)
            }
        }
    }


    @Composable
    fun SetupNavGraph(navController: NavHostController) {
        NavHost(navController = navController, startDestination = "splash_screen") {
            composable("splash_screen"){ SplashScreen(navController) }
            composable("check_user_status"){ CheckUserStatus(navController) }
            composable("login_screen") { LoginScreen(navController = navController) }
            composable("register_screen") { RegisterScreen(navController = navController) }
            composable("farmer_home") { FarmerHomeScreen(navController) }
            composable("customer_home") { CustomerHomeScreen() }
        }
    }

    @Composable
    fun CheckUserStatus(
        navController: NavController,
        viewModel: AuthenticationViewModel = AuthenticationViewModel()
    ) {

        val currentUser = remember { FirebaseAuth.getInstance().currentUser }

        var userRole by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(true) } // Loading state


        LaunchedEffect(currentUser) {
            if (currentUser != null) {
                val userId = currentUser.uid

                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val role = document.getString("role")
                        userRole = role
                        isLoading = false
                    }
                    .addOnFailureListener {
                        isLoading =
                            false
                    }
            } else {
                isLoading = false
            }
        }


        if (isLoading) {

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {

            when {
                currentUser == null -> {

                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }
                    }
                }

                userRole == "farmer" -> {
                    navController.navigate("farmer_home") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }

                userRole == "customer" -> {
                    navController.navigate("customer_home") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }

                else -> {

                    navController.navigate("role_selection_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
            }
        }
    }
}
