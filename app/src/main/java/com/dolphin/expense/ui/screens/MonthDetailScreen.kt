package com.dolphin.expense.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.dolphin.expense.data.ExpenseEntity
import com.dolphin.expense.ui.components.ExpenseItem
import com.dolphin.expense.ui.viewmodel.ExpenseViewModel
import com.dolphin.expense.utils.DateUtils
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthDetailScreen(
    year: Int,
    month: Int,
    viewModel: ExpenseViewModel,
    onBack: () -> Unit,
    onEditExpense: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    // Currency
    val currencyPreferences = remember { com.dolphin.expense.utils.CurrencyPreferences(context) }
    val currency = remember { currencyPreferences.getCurrency() }

    // Get expenses for the selected month
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
    }

    val monthStart = DateUtils.getStartOfMonth(calendar.time)
    val monthEnd = DateUtils.getEndOfMonth(calendar.time)

    val monthExpenses by viewModel.getExpensesByDateRange(monthStart, monthEnd)
        .collectAsState(initial = emptyList())

    // Filter by category if selected
    val filteredExpenses = if (selectedCategory != null) {
        monthExpenses.filter { it.category == selectedCategory }
    } else {
        monthExpenses
    }

    val totalAmount = filteredExpenses.sumOf { it.amount }

    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)

    val categories = listOf("All Categories", "Food", "Grocery", "Transport", "Shopping", "Bills", "Entertainment", "Health", "Others")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with back button and PDF export
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            exportMonthToPDF(context, monthName, filteredExpenses, totalAmount, currency.symbol)
                        }
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Export PDF",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Text(
                    text = monthName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Total: ${currency.symbol}${String.format("%.2f", totalAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${filteredExpenses.size} transactions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Filter
            item {
                ExposedDropdownMenuBox(
                    expanded = showCategoryMenu,
                    onExpandedChange = { showCategoryMenu = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory ?: "All Categories",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Filter by Category") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        leadingIcon = {
                            Icon(Icons.Default.FilterList, contentDescription = null)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = if (category == "All Categories") null else category
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Category Total Card with Animation
            item {
                AnimatedContent(
                    targetState = selectedCategory,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 400)
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 300)
                        )
                    },
                    label = "category_animation"
                ) { category ->
                    if (category != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                MaterialTheme.colorScheme.secondaryContainer
                                            )
                                        )
                                    )
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = when (category) {
                                            "Food" -> Icons.Default.Fastfood
                                            "Grocery" -> Icons.Default.ShoppingCart
                                            "Transport" -> Icons.Default.DirectionsCar
                                            "Shopping" -> Icons.Default.ShoppingBag
                                            "Bills" -> Icons.Default.Receipt
                                            "Entertainment" -> Icons.Default.Movie
                                            "Health" -> Icons.Default.MedicalServices
                                            "Others" -> Icons.Default.MoreHoriz
                                            else -> Icons.Default.Category
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Column {
                                        Text(
                                            text = "$category Total",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "${filteredExpenses.size} transactions",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Text(
                                    text = "${currency.symbol}${String.format("%.2f", totalAmount)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
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
                                text = "No Expenses Found",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedCategory != null)
                                    "No $selectedCategory expenses this month"
                                else
                                    "No expenses recorded for this month",
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
                        onEdit = { },
                        onDelete = { },
                        currencySymbol = currency.symbol,
                        modifier = Modifier.fillMaxWidth(),
                        showActions = false
                    )
                }
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

fun exportMonthToPDF(
    context: Context,
    monthName: String,
    expenses: List<ExpenseEntity>,
    totalAmount: Double,
    currencySymbol: String
) {
    try {
        if (expenses.isEmpty()) {
            Toast.makeText(context, "No expenses to export", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var yPosition = 50f

        // Title
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("Expense Report", 50f, yPosition, paint)
        yPosition += 40f

        // Month name
        paint.textSize = 18f
        paint.isFakeBoldText = false
        canvas.drawText(monthName, 50f, yPosition, paint)
        yPosition += 30f

        // Total
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Total: ${currencySymbol}${String.format("%.2f", totalAmount)}", 50f, yPosition, paint)
        yPosition += 40f

        // Table headers
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText("Date", 50f, yPosition, paint)
        canvas.drawText("Category", 150f, yPosition, paint)
        canvas.drawText("Amount", 300f, yPosition, paint)
        canvas.drawText("Description", 400f, yPosition, paint)
        yPosition += 20f

        // Draw line
        canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
        yPosition += 20f

        // Expense rows
        paint.textSize = 10f
        paint.isFakeBoldText = false
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        expenses.forEach { expense ->
            if (yPosition > 800) {
                pdfDocument.finishPage(page)
                val newPage = pdfDocument.startPage(pageInfo)
                yPosition = 50f
            }

            canvas.drawText(dateFormat.format(Date(expense.date)), 50f, yPosition, paint)
            canvas.drawText(expense.category, 150f, yPosition, paint)
            canvas.drawText("${currencySymbol}${String.format("%.2f", expense.amount)}", 300f, yPosition, paint)
            canvas.drawText(expense.description ?: "-", 400f, yPosition, paint)
            yPosition += 20f
        }

        pdfDocument.finishPage(page)

        // Save PDF
        val fileName = "expense_report_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        // Share PDF
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Expense Report - $monthName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Export PDF Report"))
        Toast.makeText(context, "PDF exported successfully!", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
        Toast.makeText(context, "PDF export failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
