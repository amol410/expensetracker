package com.dolphin.expense.navigation

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.dolphin.expense.ui.screens.*
import com.dolphin.expense.ui.viewmodel.ExpenseViewModel
import com.dolphin.expense.ui.viewmodel.MainViewModel
import com.dolphin.expense.utils.CurrencyPreferences
import androidx.hilt.navigation.compose.hiltViewModel
import com.dolphin.expense.data.ExpenseEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val authViewModel: com.dolphin.expense.ui.viewmodel.AuthViewModel = hiltViewModel()
    val currencyPreferences: CurrencyPreferences = mainViewModel.currencyPreferences
    val context = LocalContext.current

    // Check authentication status and first launch
    val isAuthenticated = remember { authViewModel.isUserAuthenticated() }
    val isFirstLaunch = remember { currencyPreferences.isFirstLaunch() }

    val startDestination = when {
        !isAuthenticated -> Screen.Login.route
        isFirstLaunch -> Screen.Welcome.route
        else -> Screen.Dashboard.route
    }

    // Bottom navigation items
    val bottomNavigationItems = listOf(
        BottomNavigationItem(
            route = Screen.Dashboard.route,
            icon = Icons.Default.Home,
            title = "Home"
        ),
        BottomNavigationItem(
            route = Screen.AllExpenses.route,
            icon = Icons.Default.List,
            title = "Expenses"
        ),
        BottomNavigationItem(
            route = Screen.Statistics.route,
            icon = Icons.Default.BarChart,
            title = "Statistics"
        ),
        BottomNavigationItem(
            route = Screen.Settings.route,
            icon = Icons.Default.Settings,
            title = "Settings"
        )
    )

    // Determine if bottom bar should be shown
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.AllExpenses.route,
        Screen.Statistics.route,
        Screen.Settings.route
    )
    
    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavigationItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        val destination = if (currencyPreferences.isFirstLaunch()) {
                            Screen.Welcome.route
                        } else {
                            Screen.Dashboard.route
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Onboarding Screens
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Screen.CurrencySelection.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.CurrencySelection.route) {
                CurrencySelectionScreen(
                    onCurrencySelected = { currency ->
                        currencyPreferences.saveCurrency(currency)
                        currencyPreferences.setFirstLaunchComplete()
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.CurrencySelection.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) { 
                val viewModel: MainViewModel = hiltViewModel()
                DashboardScreen(
                    viewModel = viewModel,
                    onAddExpenseClick = {
                        navController.navigate(Screen.AddEditExpense.route)
                    },
                    onExpenseClick = { expenseId ->
                        navController.navigate(Screen.AddEditExpenseWithId.createRoute(expenseId))
                    }
                )
            }
            
            composable(Screen.AllExpenses.route) {
                val viewModel: ExpenseViewModel = hiltViewModel()
                AllExpensesScreen(
                    viewModel = viewModel,
                    onAddExpenseClick = {
                        navController.navigate(Screen.AddEditExpense.route)
                    },
                    onEditExpenseClick = { expenseId ->
                        navController.navigate(Screen.AddEditExpenseWithId.createRoute(expenseId))
                    }
                )
            }
            
            composable(Screen.Statistics.route) {
                val viewModel: ExpenseViewModel = hiltViewModel()
                StatisticsScreen(
                    viewModel = viewModel,
                    onMonthClick = { year, month ->
                        navController.navigate(Screen.MonthDetail.createRoute(year, month))
                    }
                )
            }

            composable(Screen.Settings.route) {
                val viewModel: MainViewModel = hiltViewModel()
                var showCurrencyDialog by remember { mutableStateOf(false) }
                var showChangeCurrencyWarning by remember { mutableStateOf(false) }

                SettingsScreen(
                    currencyPreferences = currencyPreferences,
                    onCurrencyChangeClick = {
                        showChangeCurrencyWarning = true
                    },
                    onClearData = {
                        viewModel.clearAllData()
                        currencyPreferences.clear()
                        (context as? Activity)?.finish()
                    },
                    onSignOut = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )

                // Currency Change Warning Dialog
                if (showChangeCurrencyWarning) {
                    val currentCurrency = currencyPreferences.getCurrency()
                    AlertDialog(
                        onDismissRequest = { showChangeCurrencyWarning = false },
                        icon = {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = {
                            Text("Change Currency?", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        },
                        text = {
                            Column {
                                Text("Current: ${currentCurrency.flag} ${currentCurrency.symbol} (${currentCurrency.code})")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "⚠️ Important Note:\nExisting expense amounts will NOT be converted. They will simply display with the new currency symbol.\n\nExample:\nBefore: ${currentCurrency.symbol}500\nAfter: [New Symbol]500",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                showChangeCurrencyWarning = false
                                navController.navigate(Screen.CurrencySelection.route)
                            }) {
                                Text("Continue")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showChangeCurrencyWarning = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }

            composable(
                route = Screen.MonthDetail.route,
                arguments = listOf(
                    androidx.navigation.navArgument("year") { type = androidx.navigation.NavType.IntType },
                    androidx.navigation.navArgument("month") { type = androidx.navigation.NavType.IntType }
                )
            ) { backStackEntry ->
                val year = backStackEntry.arguments?.getInt("year") ?: 0
                val month = backStackEntry.arguments?.getInt("month") ?: 0
                val viewModel: ExpenseViewModel = hiltViewModel()
                MonthDetailScreen(
                    year = year,
                    month = month,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEditExpense = { expenseId ->
                        navController.navigate(Screen.AddEditExpenseWithId.createRoute(expenseId))
                    }
                )
            }
            
            composable(Screen.AddEditExpense.route) {
                val expenseViewModel: ExpenseViewModel = hiltViewModel()
                AddEditExpenseScreen(
                    expense = null,
                    onSave = { expense ->
                        expenseViewModel.addExpense(expense)
                        navController.popBackStack()
                    },
                    onBack = { 
                        navController.popBackStack()
                    }
                )
            }
            
            composable(
                route = Screen.AddEditExpenseWithId.route,
                arguments = listOf(
                    androidx.navigation.navArgument("expenseId") { 
                        type = androidx.navigation.NavType.LongType 
                    }
                )
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: 0L
                val expenseViewModel: ExpenseViewModel = hiltViewModel()
                
                // Get the expense from ViewModel
                var expense by remember { mutableStateOf<ExpenseEntity?>(null) }
                
                LaunchedEffect(expenseId) {
                    expense = expenseViewModel.getExpenseById(expenseId)
                }
                
                if (expense != null) {
                    AddEditExpenseScreen(
                        expense = expense!!,
                        onSave = { updatedExpense ->
                            expenseViewModel.updateExpense(updatedExpense)
                            navController.popBackStack()
                        },
                        onBack = {
                            navController.popBackStack()
                        },
                        onDelete = { expenseToDelete ->
                            expenseViewModel.deleteExpense(expenseToDelete)
                            navController.popBackStack()
                        }
                    )
                } else {
                    // Loading state
                    Text("Loading expense...")
                }
            }
        }
    }
}

data class BottomNavigationItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String
)