package io.iskopasi.kmpvpntest.managers

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

@Entity(
    tableName = "domains",
    indices = [Index(value = ["domain"], unique = true)]
)
data class Domain(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val domain: String,
    val added: Long = System.currentTimeMillis()
)

@Database(entities = [Domain::class], version = 1, exportSchema = false)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFilterDao(): FilterDao
}

@Suppress("KotlinNoActualForExpect") // The Room compiler generates the `actual` implementations.
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@Dao
interface FilterDao {
    @Query("SELECT * FROM domains ORDER BY added DESC")
    suspend fun getDomains(): List<Domain>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: Domain)

    @Delete
    suspend fun delete(domain: Domain)
}