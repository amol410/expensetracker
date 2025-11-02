package com.dolphin.expense.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin
import androidx.core.content.FileProvider
import com.dolphin.expense.data.ExpenseEntity
import com.dolphin.expense.ui.components.ExpenseItem
import com.dolphin.expense.ui.viewmodel.ExpenseViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpensesScreen(
    viewModel: ExpenseViewModel,
    onAddExpenseClick: () -> Unit,
    onEditExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showExportMenu by remember { mutableStateOf(false) }

    // Currency
    val currencyPreferences = remember { com.dolphin.expense.utils.CurrencyPreferences(context) }
    val currency = remember { currencyPreferences.getCurrency() }

    // Get current month budget and expenses
    val currentMonthBudgets by viewModel.getBudgetsForCurrentMonth().collectAsState(initial = emptyList())
    val currentMonthExpenses by viewModel.getCurrentMonthExpenses().collectAsState(initial = emptyList())
    val currentMonthTotal = currentMonthExpenses.sumOf { it.amount }
    val monthlyBudget = currentMonthBudgets.firstOrNull()

    // Budget dialog state
    var showBudgetDialog by remember { mutableStateOf(false) }
    var budgetAmount by remember { mutableStateOf("") }

    // Filter expenses based on search query
    val filteredExpenses = remember(uiState.expenses, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.expenses
        } else {
            uiState.expenses.filter { expense ->
                expense.category.contains(searchQuery, ignoreCase = true) ||
                expense.description?.contains(searchQuery, ignoreCase = true) == true ||
                expense.amount.toString().contains(searchQuery)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "All Expenses",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Track your spending",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            exportExpensesToCSV(context, uiState.expenses)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export CSV",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search expenses...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true
                )
            }

            // Monthly Budget Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF11998E),
                                        Color(0xFF38EF7D)
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Monthly Budget",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (monthlyBudget != null)
                                        "${currency.symbol}${String.format("%.2f", monthlyBudget.monthlyLimit)}"
                                    else
                                        "Not Set",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (monthlyBudget != null) {
                                    IconButton(
                                        onClick = {
                                            budgetAmount = monthlyBudget.monthlyLimit.toString()
                                            showBudgetDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Budget",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteBudget(monthlyBudget)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Budget",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            budgetAmount = ""
                                            showBudgetDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Set Budget",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Total Spent (This Month) Card with Animated Fill
            item {
                val spentPercentage = if (monthlyBudget != null && monthlyBudget.monthlyLimit > 0) {
                    (currentMonthTotal / monthlyBudget.monthlyLimit).toFloat().coerceIn(0f, 1f)
                } else {
                    0f
                }

                val animatedPercentage by animateFloatAsState(
                    targetValue = spentPercentage,
                    animationSpec = tween(durationMillis = 1000),
                    label = "budget_fill"
                )

                // Dynamic color based on percentage
                val fillColor = when {
                    spentPercentage < 0.5f -> Color(0xFF4CAF50) // Green
                    spentPercentage < 0.75f -> Color(0xFFFFA726) // Orange
                    else -> Color(0xFFEF5350) // Red
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        // Background gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF2C3E50),
                                            Color(0xFF34495E)
                                        )
                                    )
                                )
                        )

                        // Animated wavy fill from bottom
                        WavyFill(
                            fillPercentage = animatedPercentage,
                            fillColor = fillColor,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.BottomCenter)
                        )

                        // Content overlay
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Total Spent (This Month)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${currency.symbol}${String.format("%.2f", currentMonthTotal)}",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (monthlyBudget != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val remaining = monthlyBudget.monthlyLimit - currentMonthTotal
                                    Text(
                                        text = if (remaining >= 0)
                                            "${currency.symbol}${String.format("%.2f", remaining)} remaining"
                                        else
                                            "${currency.symbol}${String.format("%.2f", -remaining)} over budget",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (remaining >= 0) Color.White.copy(alpha = 0.8f) else Color(0xFFFFCDD2),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${(spentPercentage * 100).toInt()}% of budget",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.White.copy(alpha = 0.9f)
                                )
                                if (monthlyBudget != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(30.dp))
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${(spentPercentage * 100).toInt()}%",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Expenses Count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions (${filteredExpenses.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Expenses List
            if (filteredExpenses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No Results" else "No Expenses Found",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty())
                                    "Try different keywords"
                                else
                                    "Start tracking by adding your first expense!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredExpenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        onEdit = { onEditExpenseClick(it.id) },
                        onDelete = { viewModel.deleteExpense(it) },
                        currencySymbol = currency.symbol,
                        modifier = Modifier.fillMaxWidth(),
                        showActions = true
                    )
                }
            }

            // Bottom padding for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Budget Dialog
        if (showBudgetDialog) {
            AlertDialog(
                onDismissRequest = { showBudgetDialog = false },
                title = {
                    Text(
                        text = if (monthlyBudget != null) "Edit Monthly Budget" else "Set Monthly Budget",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Enter your budget amount for this month:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = budgetAmount,
                            onValueChange = { budgetAmount = it },
                            label = { Text("Amount") },
                            leadingIcon = {
                                Text(currency.symbol, style = MaterialTheme.typography.titleMedium)
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = budgetAmount.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                if (monthlyBudget != null) {
                                    viewModel.updateBudget(monthlyBudget, amount)
                                } else {
                                    viewModel.insertBudget(amount)
                                }
                                showBudgetDialog = false
                                budgetAmount = ""
                            }
                        }
                    ) {
                        Text(if (monthlyBudget != null) "Update" else "Set")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showBudgetDialog = false
                        budgetAmount = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddExpenseClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Expense",
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun WavyFill(
    fillPercentage: Float,
    fillColor: Color,
    modifier: Modifier = Modifier
) {
    // Infinite wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val fillHeight = height * fillPercentage

        if (fillPercentage > 0f) {
            val path = Path()

            // Wave parameters
            val waveAmplitude = 15f
            val waveLength = width / 2f
            val waveSpeed = waveOffset * waveLength

            // Start from bottom left
            path.moveTo(0f, height)

            // Draw left edge up to fill height
            path.lineTo(0f, height - fillHeight)

            // Draw wavy top edge
            var x = 0f
            while (x <= width) {
                val relativeX = x / waveLength
                val wave1 = sin((relativeX + waveSpeed / waveLength) * 2 * PI).toFloat()
                val wave2 = sin((relativeX * 1.5 + waveSpeed / waveLength * 0.8) * 2 * PI).toFloat()
                val y = height - fillHeight + (wave1 * waveAmplitude * 0.6f) + (wave2 * waveAmplitude * 0.4f)

                if (x == 0f) {
                    path.lineTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                x += 5f
            }

            // Complete the path back to bottom
            path.lineTo(width, height)
            path.close()

            // Draw the filled area with gradient
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        fillColor.copy(alpha = 0.5f),
                        fillColor.copy(alpha = 0.8f)
                    ),
                    startY = height - fillHeight,
                    endY = height
                ),
                style = Fill
            )

            // Draw a second wave layer for depth
            val path2 = Path()
            path2.moveTo(0f, height)
            path2.lineTo(0f, height - fillHeight)

            x = 0f
            while (x <= width) {
                val relativeX = x / waveLength
                val wave1 = sin((relativeX - waveSpeed / waveLength * 1.2) * 2 * PI).toFloat()
                val wave2 = sin((relativeX * 1.8 - waveSpeed / waveLength) * 2 * PI).toFloat()
                val y = height - fillHeight + (wave1 * waveAmplitude * 0.5f) + (wave2 * waveAmplitude * 0.5f)

                path2.lineTo(x, y)
                x += 5f
            }

            path2.lineTo(width, height)
            path2.close()

            drawPath(
                path = path2,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        fillColor.copy(alpha = 0.3f),
                        fillColor.copy(alpha = 0.6f)
                    ),
                    startY = height - fillHeight,
                    endY = height
                ),
                style = Fill
            )
        }
    }
}

fun exportExpensesToCSV(context: Context, expenses: List<ExpenseEntity>) {
    try {
        if (expenses.isEmpty()) {
            Toast.makeText(context, "No expenses to export", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fileName = "expenses_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"

        val csvContent = buildString {
            appendLine("Date,Category,Amount,Description")
            expenses.forEach { expense ->
                val date = dateFormat.format(Date(expense.date))
                val category = expense.category
                val amount = String.format("%.2f", expense.amount)
                val description = expense.description?.replace(",", ";") ?: ""
                appendLine("$date,$category,$amount,\"$description\"")
            }
        }

        val file = File(context.cacheDir, fileName)
        file.writeText(csvContent)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Expense Report")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Export Expenses"))
        Toast.makeText(context, "Exporting ${expenses.size} expenses...", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
