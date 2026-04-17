package com.institute.ims.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.institute.ims.data.repository.FakeUserRepository
import com.institute.ims.ui.common.CapabilityInfoScreen
import com.institute.ims.ui.common.RoleSelectScreen
import com.institute.ims.ui.common.SplashScreen
import com.institute.ims.ui.dashboard.DashboardScreen
import com.institute.ims.ui.dashboard.NewsScreen
import com.institute.ims.ui.examinations.CreateExamScreen
import com.institute.ims.ui.examinations.ExamDetailScreen
import com.institute.ims.ui.examinations.ExamListScreen
import com.institute.ims.ui.examinations.ReportScreen
import com.institute.ims.ui.settings.RegionalSettingsScreen
import com.institute.ims.ui.studentdetails.StudentListScreen
import com.institute.ims.ui.studentdetails.StudentProfileScreen

/** Root navigation graph: splash → role → dashboard; stack routes for students, exams, create, detail, report. */
@Composable
fun ImsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash,
        modifier = modifier,
    ) {
        composable(NavRoutes.Splash) {
            SplashScreen(
                onContinueToRoleSelect = {
                    navController.navigate(NavRoutes.RoleSelect) {
                        popUpTo(NavRoutes.Splash) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoutes.RoleSelect) {
            RoleSelectScreen(
                users = FakeUserRepository.getSelectableUsers(),
                onUserSelected = { user ->
                    navController.navigate(NavRoutes.dashboard(user.id)) {
                        popUpTo(NavRoutes.RoleSelect) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = NavRoutes.Dashboard,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
            ),
        ) { entry ->
            val rawId = entry.arguments?.getString("userId").orEmpty()
            val userId = android.net.Uri.decode(rawId)
            val user = remember(userId) { FakeUserRepository.getUser(userId) }

            if (user == null) {
                LaunchedEffect(userId) {
                    navController.navigate(NavRoutes.RoleSelect) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                Box(modifier = Modifier)
            } else {
                DashboardScreen(
                    userId = user.id,
                    onSignOut = {
                        navController.navigate(NavRoutes.RoleSelect) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onSwitchRole = {
                        navController.navigate(NavRoutes.RoleSelect) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onOpenStudents = { navController.navigate(NavRoutes.StudentList) },
                    onOpenExams = { navController.navigate(NavRoutes.ExamList) },
                    onOpenNews = { query ->
                        navController.navigate(NavRoutes.news(query))
                    },
                    onOpenStudentProfile = { studentId ->
                        navController.navigate(NavRoutes.studentProfile(studentId))
                    },
                    onOpenExamDetail = { examId ->
                        navController.navigate(NavRoutes.examDetail(examId))
                    },
                    onOpenRegionalSettings = {
                        navController.navigate(NavRoutes.RegionalSettings)
                    },
                    onOpenCapabilityInfo = { stubId ->
                        navController.navigate(NavRoutes.capabilityInfo(stubId))
                    },
                )
            }
        }

        composable(
            route = NavRoutes.CapabilityInfo,
            arguments = listOf(
                navArgument("stubId") { type = NavType.StringType },
            ),
        ) { entry ->
            val raw = entry.arguments?.getString("stubId").orEmpty()
            val stubId = android.net.Uri.decode(raw)
            CapabilityInfoScreen(
                stubId = stubId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.RegionalSettings) {
            RegionalSettingsScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(NavRoutes.RoleSelect) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(NavRoutes.StudentList) {
            StudentListScreen(
                onBack = { navController.popBackStack() },
                onOpenProfile = { studentId ->
                    navController.navigate(NavRoutes.studentProfile(studentId))
                },
            )
        }

        composable(
            route = NavRoutes.StudentProfile,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
            ),
        ) { entry ->
            val rawId = entry.arguments?.getString("studentId").orEmpty()
            val studentId = android.net.Uri.decode(rawId)
            StudentProfileScreen(
                studentId = studentId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.ExamList) {
            ExamListScreen(
                onBack = { navController.popBackStack() },
                onCreateExam = { navController.navigate(NavRoutes.CreateExam) },
                onOpenExam = { examId ->
                    navController.navigate(NavRoutes.examDetail(examId))
                },
            )
        }

        composable(
            route = NavRoutes.News,
            arguments = listOf(
                navArgument("query") { type = NavType.StringType },
            ),
        ) { entry ->
            val rawQuery = entry.arguments?.getString("query").orEmpty()
            val query = android.net.Uri.decode(rawQuery)
            NewsScreen(
                initialQuery = query,
                onBack = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.CreateExam) {
            CreateExamScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable(
            route = NavRoutes.ExamDetail,
            arguments = listOf(
                navArgument("examId") { type = NavType.StringType },
            ),
        ) { entry ->
            val rawId = entry.arguments?.getString("examId").orEmpty()
            val examId = android.net.Uri.decode(rawId)
            ExamDetailScreen(
                examId = examId,
                onBack = { navController.popBackStack() },
                onOpenReport = {
                    navController.navigate(NavRoutes.report(examId))
                },
            )
        }

        composable(
            route = NavRoutes.Report,
            arguments = listOf(
                navArgument("examId") { type = NavType.StringType },
            ),
        ) { entry ->
            val rawId = entry.arguments?.getString("examId").orEmpty()
            val examId = android.net.Uri.decode(rawId)
            ReportScreen(
                examId = examId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
