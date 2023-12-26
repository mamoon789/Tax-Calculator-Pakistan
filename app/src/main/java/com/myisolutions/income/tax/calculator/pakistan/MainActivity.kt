package com.myisolutions.income.tax.calculator.pakistan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.myisolutions.income.tax.calculator.pakistan.ui.theme.TaxCalculatorPakistanTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.composable

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App()
{
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController = navController, startDestination = "main") {
        navigation(
            startDestination = "tax_screen",
            route = "main"
        ) {
            composable(
                route = "tax_screen",
                exitTransition = {
                    slideOutVertically(tween(500)) { -2000 }
                },
                popEnterTransition = {
                    slideInVertically(tween(500)) { -2000 }
                }
            ) {
                val viewModel = it.sharedViewModel<MainViewModel>(navController)
                TaxScreen(viewModel) {
                    navController.navigate("tax_detail_screen")
                }
            }
            composable(
                route = "tax_detail_screen",
                enterTransition = {
                    slideInVertically(tween(500)) { 2000 }
                },
                popExitTransition = {
                    slideOutVertically(tween(500)) { 2000 }
                }
            ) {
                val viewModel = it.sharedViewModel<MainViewModel>(navController)
                TaxDetailScreen(viewModel) {
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T
{
    val parentRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(parentRoute)
    }
    return viewModel(parentEntry)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview()
{
    TaxCalculatorPakistanTheme {
    }
}