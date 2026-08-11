package com.example.farmastudy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.farmastudy.ui.AuthViewModel
import com.example.farmastudy.ui.screens.home.HomeScreen
import com.example.farmastudy.ui.screens.login.LoginScreen
import com.example.farmastudy.ui.screens.register.RegisterScreen

@Composable
fun FarmaNavGraph(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    val session by authViewModel.session.collectAsState(initial = null)

    if (session == null) {
        AuthNavGraph(
            navController = rememberNavController(),
            authViewModel = authViewModel,
            modifier = modifier
        )
    } else {
        HomeNavGraph(
            navController = rememberNavController(),
            authViewModel = authViewModel,
            username = session ?: "",
            modifier = modifier
        )
    }
}

@Composable
private fun AuthNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val state by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                state = state,
                onLogin = authViewModel::login,
                onGoToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                state = state,
                onRegister = authViewModel::register,
                onBackToLogin = {
                    authViewModel.clearMessages()
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun HomeNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    username: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                username = username,
                onStudyByClassification = {},
                onRandomStudy = {},
                onQuiz = {},
                onLogout = authViewModel::logout
            )
        }
    }
}