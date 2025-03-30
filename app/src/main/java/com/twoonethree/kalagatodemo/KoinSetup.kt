package com.twoonethree.kalagatodemo

import com.twoonethree.kalagatodemo.room.AppDatabase
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { createDatabase(androidApplication()) }
    single { get<AppDatabase>().taskDao() }
    single { TaskRepository(get()) }
    single { LocalDataStore(get()) }
    viewModel { TaskViewModel(get()) }
}

private fun createDatabase(app: android.app.Application): AppDatabase {
    return Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "task_database"
    ).build()
}