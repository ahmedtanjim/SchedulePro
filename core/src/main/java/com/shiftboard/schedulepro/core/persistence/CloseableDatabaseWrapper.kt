package com.shiftboard.schedulepro.core.persistence

import android.content.Context
import androidx.room.RoomDatabase
import java.io.File
import java.io.FileFilter
import java.util.concurrent.atomic.AtomicReference

abstract class CloseableDatabaseWrapper<out T: RoomDatabase>(protected val context: Context) {
    private var _database = AtomicReference<T?>()

    @Synchronized
    open fun getDatabase() = _database.get() ?: createAndSetDatabase()

    protected abstract fun createDatabase(): T

    private fun createAndSetDatabase(): T {
        val database = createDatabase()
        _database.set(database)
        return database
    }

    @Synchronized
    open fun closeDatabase(deleteFile: Boolean = false) {
        try {
            val database = _database.get()
            database?.let {
                val path = it.openHelper.writableDatabase.path
                it.close()
                if (deleteFile) {
                    deleteDatabaseFiles(File(path))
                }
            }
        } finally {
            _database.set(null)
        }
    }

    private fun deleteDatabaseFiles(file: File): Boolean {
        var deleted: Boolean
        deleted = file.delete()
        deleted = deleted or File("${file.path}-journal").delete()
        deleted = deleted or File("${file.path}-shm").delete()
        deleted = deleted or File("${file.path}-wal").delete()

        val dir = file.parentFile
        if (dir != null && dir.exists()) {
            val prefix = "${file.name}-mj"
            val filter = FileFilter { candidate -> candidate.name.startsWith(prefix) }
            val files = dir.listFiles(filter) ?: emptyArray()
            for (masterJournal in files) {
                deleted = deleted or masterJournal.delete()
            }
        }
        return deleted
    }
}