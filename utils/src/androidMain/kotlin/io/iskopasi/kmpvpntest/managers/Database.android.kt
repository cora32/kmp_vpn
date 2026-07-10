package io.iskopasi.kmpvpntest.managers

import androidx.room.Room
import androidx.room.RoomDatabase
import io.iskopasi.kmpvpntest.api.AppContext

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appContext = AppContext.get()
    val dbFile = appContext.getDatabasePath("app_database.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}