package org.delcom.pam_p4_ifs23009.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.delcom.pam_p4_ifs23009.helper.ConstHelper
import org.delcom.pam_p4_ifs23009.ui.components.CustomSnackbar
import org.delcom.pam_p4_ifs23009.ui.screens.*
import org.delcom.pam_p4_ifs23009.ui.viewmodels.FishViewModel
import org.delcom.pam_p4_ifs23009.ui.viewmodels.PlantViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UIApp(
    navController: NavHostController = rememberNavController(),
    plantViewModel: PlantViewModel,
    fishViewModel: FishViewModel
) {
    // Inisialisasi SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState){ snackbarData ->
            CustomSnackbar(snackbarData, onDismiss = { snackbarHostState.currentSnackbarData?.dismiss() })
        } },
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = ConstHelper.RouteNames.Home.path,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))

        ) {
            // Home
            composable(
                route = ConstHelper.RouteNames.Home.path,
            ) { _ ->
                HomeScreen(
                    navController = navController,
                )
            }

            // Profile
            composable(
                route = ConstHelper.RouteNames.Profile.path,
            ) { _ ->
                ProfileScreen(
                    navController = navController,
                    plantViewModel = plantViewModel
                )
            }

            // Plants
            composable(
                route = ConstHelper.RouteNames.Plants.path,
            ) { _ ->
                PlantsScreen(
                    navController = navController,
                    plantViewModel = plantViewModel
                )
            }

            // Plants Add
            composable(
                route = ConstHelper.RouteNames.PlantsAdd.path,
            ) { _ ->
                PlantsAddScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    plantViewModel = plantViewModel
                )
            }

            // Plants Detail
            composable(
                route = ConstHelper.RouteNames.PlantsDetail.path,
                arguments = listOf(
                    navArgument("plantId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""

                PlantsDetailScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    plantViewModel = plantViewModel,
                    plantId = plantId
                )
            }

            // Plants Edit
            composable(
                route = ConstHelper.RouteNames.PlantsEdit.path,
                arguments = listOf(
                    navArgument("plantId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""

                PlantsEditScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    plantViewModel = plantViewModel,
                    plantId = plantId
                )
            }

            // Fishes
            composable(
                route = ConstHelper.RouteNames.Fishes.path,
            ) { _ ->
                FishesScreen(
                    navController = navController,
                    fishViewModel = fishViewModel
                )
            }

            // Fishes Add
            composable(
                route = ConstHelper.RouteNames.FishesAdd.path,
            ) { _ ->
                FishesAddScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    fishViewModel = fishViewModel
                )
            }

            // Fishes Detail
            composable(
                route = ConstHelper.RouteNames.FishesDetail.path,
                arguments = listOf(
                    navArgument("fishId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val fishId = backStackEntry.arguments?.getString("fishId") ?: ""

                FishesDetailScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    fishViewModel = fishViewModel,
                    fishId = fishId
                )
            }

            // Fishes Edit
            composable(
                route = ConstHelper.RouteNames.FishesEdit.path,
                arguments = listOf(
                    navArgument("fishId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val fishId = backStackEntry.arguments?.getString("fishId") ?: ""

                FishesEditScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    fishViewModel = fishViewModel,
                    fishId = fishId
                )
            }
        }
    }
}
