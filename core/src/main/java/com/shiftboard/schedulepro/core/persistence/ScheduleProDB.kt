package com.shiftboard.schedulepro.core.persistence

import android.app.Application
import androidx.room.Room
import com.shiftboard.schedulepro.core.persistence.dao.DetailsDao
import com.shiftboard.schedulepro.core.persistence.dao.ScheduleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.dsl.module
import timber.log.Timber
import java.io.File


class ScheduleProDB(
    private val application: Application,
    private val creds: DatabaseCredentialsProvider,
) : CloseableDatabaseWrapper<AppDatabase>(application) {

    fun detailsDao(): DetailsDao = getDatabase().detailsDao()
    fun scheduleDao(): ScheduleDao = getDatabase().scheduleDao()

    suspend fun testIntegrity() {
        withContext(Dispatchers.IO) {
            Timber.d("TESTING INTEGRITY")
            try {
                val results = getDatabase().query("SELECT * FROM room_master_table", null)
                Timber.d("INTEGRITY RESULTS: $results")
            } catch (e: Exception) {
                Timber.d("CAUGHT EXCEPTION")
                File(application.getDatabasePath(AppDatabase.name).absolutePath).deleteDatabaseFile()
            }
        }
    }

    private fun File.deleteDatabaseFile() {
        try {
            this.takeIf { it.exists() }?.delete()
            File("${path}-journal").takeIf { it.exists() }?.delete()
            File("${path}-shm").takeIf { it.exists() }?.delete()
            File("${path}-wal").takeIf { it.exists() }?.delete()
        } catch (ignore: Exception) {
            // IGNORE Exception
        }
    }

    override fun createDatabase(): AppDatabase {
//        val passphrase = SQLiteDatabase.getBytes(creds.databaseCredentials().toCharArray())
//        val factory = SupportFactory(passphrase, object: SQLiteDatabaseHook {
//            override fun preKey(database: SQLiteDatabase?) {
//
//            }
//
//            override fun postKey(database: SQLiteDatabase?) {
//
//            }
//        }, false)

        return Room.databaseBuilder(context, AppDatabase::class.java, "shiftboard-db")
            .fallbackToDestructiveMigration()
//            .openHelperFactory(factory)
            .build()
    }

    companion object {
        val module = module {
            factory { get<ScheduleProDB>().scheduleDao() }
            factory { get<ScheduleProDB>().detailsDao() }

            factory { get<ScheduleProDB>().getDatabase() }

            single { ScheduleProDB(get(), get()) }
        }
    }
}

interface DatabaseCredentialsProvider {
    fun databaseCredentials(): String
}