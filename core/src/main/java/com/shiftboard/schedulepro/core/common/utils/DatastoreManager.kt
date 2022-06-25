package com.shiftboard.schedulepro.core.common.utils

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.Serializer
import androidx.datastore.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.properties.Delegates

sealed class DatastoreManager {

    protected fun <T> createDatastore(filename: String, serializer: Serializer<T>): DataStore<T> {
        return context.createDataStore(filename, serializer)
    }

    companion object {
        private var context: Context by Delegates.notNull()

        fun init(context: Context) {
            DatastoreManager.context = context
        }
    }
}

abstract class DatastoreContainer<T>(filename: String, serializer: Serializer<T>): DatastoreManager() {
    val datastore: DataStore<T> = createDatastore(filename, serializer)
}