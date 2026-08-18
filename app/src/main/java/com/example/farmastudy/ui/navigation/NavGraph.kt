package com.example.farmastudy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.farmastudy.ui.AuthViewModel
import com.example.farmastudy.ui.QuizViewModel
import com.example.farmastudy.ui.StudyViewModel
import com.example.farmastudy.ui.screens.classification.ClassificationScreen
import com.example.farmastudy.ui.screens.history.QuizHistoryScreen
import com.example.farmastudy.ui.screens.home.HomeScreen
import com.example.farmastudy.ui.screens.login.LoginScreen
import com.example.farmastudy.ui.screens.quiz.QuizIntroScreen
import com.example.farmastudy.ui.screens.quiz.QuizQuestionScreen
import com.example.farmastudy.ui.screens.quiz.QuizResultScreen
import com.example.farmastudy.ui.screens.random.RandomStudyScreen
import com.example.farmastudy.ui.screens.register.RegisterScreen
import com.example.farmastudy.ui.screens.study.StudyByCategoryScreen

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
    val studyViewModel: StudyViewModel = viewModel()
    val quizViewModel: QuizViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            val stats by quizViewModel.statsFor(username).collectAsState(initial = null)
            HomeScreen(
                username = username,
                stats = stats,
                onStudyByClassification = { navController.navigate(Routes.CLASSIFICATION) },
                onRandomStudy = { navController.navigate(Routes.RANDOM_STUDY) },
                onQuiz = { navController.navigate(Routes.quizIntro(0)) },
                onHistory = { navController.navigate(Routes.QUIZ_HISTORY) },
                onLogout = authViewModel::logout
            )
        }
        composable(Routes.QUIZ_HISTORY) {
            QuizHistoryScreen(
                username = username,
                viewModel = quizViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CLASSIFICATION) {
            ClassificationScreen(
                onSelectCategory = { category ->
                    navController.navigate(Routes.studyByCategory(category))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.STUDY_BY_CATEGORY,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { entry ->
            StudyByCategoryScreen(
                category = entry.arguments?.getString("category").orEmpty(),
                viewModel = studyViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.RANDOM_STUDY) {
            RandomStudyScreen(
                username = username,
                viewModel = studyViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.QUIZ_INTRO,
            arguments = listOf(navArgument("quizPart") { type = NavType.IntType })
        ) { entry ->
            QuizIntroScreen(
                quizPart = entry.arguments?.getInt("quizPart") ?: 0,
                viewModel = quizViewModel,
                onStartQuiz = { part -> navController.navigate(Routes.quizQuestion(part, 0)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.QUIZ_QUESTION,
            arguments = listOf(
                navArgument("quizPart") { type = NavType.IntType },
                navArgument("questionIndex") { type = NavType.IntType }
            )
        ) { entry ->
            val quizPart = entry.arguments?.getInt("quizPart") ?: 0
            val questionIndex = entry.arguments?.getInt("questionIndex") ?: 0
            QuizQuestionScreen(
                quizPart = quizPart,
                questionIndex = questionIndex,
                viewModel = quizViewModel,
                onNext = { nextIndex ->
                    if (nextIndex >= quizViewModel.uiState.value.questions.size) {
                        quizViewModel.recordAttempt(username)
                        navController.navigate(Routes.QUIZ_RESULT) {
                            popUpTo(Routes.QUIZ_QUESTION) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.quizQuestion(quizPart, nextIndex)) {
                            popUpTo(Routes.QUIZ_QUESTION) { inclusive = true }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.QUIZ_RESULT) {
            QuizResultScreen(
                viewModel = quizViewModel,
                onRetry = { part ->
                    navController.navigate(Routes.quizIntro(part)) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onBackHome = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }
    }
}