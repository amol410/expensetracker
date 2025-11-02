package com.dolphin.expense.di

import android.content.Context
import com.dolphin.expense.data.database.ExpenseDatabase
import com.dolphin.expense.data.repository.ExpenseRepository
import com.dolphin.expense.data.repository.ExpenseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideExpenseDatabase(@ApplicationContext context: Context): ExpenseDatabase {
        return ExpenseDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideExpenseDao(database: ExpenseDatabase) = database.expenseDao()

    @Provides
    @Singleton
    fun provideBudgetDao(database: ExpenseDatabase) = database.budgetDao()

    @Provides
    @Singleton
    fun provideExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository {
        return impl
    }
}