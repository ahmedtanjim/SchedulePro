@file:Suppress(
    "LeakingThis", "MemberVisibilityCanPrivate", "unused", "NOTHING_TO_INLINE", "FunctionName",
    "MemberVisibilityCanBePrivate"
)

package com.shiftboard.schedulepro.core.common.utils


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Build
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.File
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


/**
 * Copyright 2020 Camaron Crowe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
sealed class PrefsManager {
    protected fun addManagedContainer(container: PrefsContainer) {
        if (!prefsMap.containsKey(container.namespace)) {
            prefsMap[container.namespace] = container
        } else {
            if (!(prefsMap[container.namespace] === container))
                throw RuntimeException("A namespace with the key ${container.namespace} already exists.")
        }
    }

    protected fun addEncryptedManagedContainer(container: EncryptedPrefsContainer) {
        if (!encryptedPrefsMap.containsKey(container.namespace)) {
            encryptedPrefsMap[container.namespace] = container
        } else {
            if (!(encryptedPrefsMap[container.namespace] === container)) {
                throw RuntimeException("A namespace with the key ${container.namespace} already exists.")
            }
        }
    }

    protected fun getManagedContainer(namespace: String): SharedPreferences {
        prefsMap[namespace]!!.let {
            return if (namespace == COMMON_NAMESPACE) {
                PreferenceManager.getDefaultSharedPreferences(context)
            } else {
                context.getSharedPreferences("${context.packageName}.${it.namespace}", it.privacy)
            }
        }
    }

    protected fun getEncryptedMangedContainer(namespace: String): SharedPreferences {
        encryptedPrefsMap[namespace]!!.let { _ ->
            return try {
                val prefs = createEncryptedNamespace(namespace)
                prefs.verify()
                prefs
            } catch (e: Exception) {
                deleteNamespace(namespace)
                createEncryptedNamespace(namespace)
            }
        }
    }

    protected fun getAsUnencrypted(namespace: String): SharedPreferences {
        return if (namespace == COMMON_NAMESPACE) {
            PreferenceManager.getDefaultSharedPreferences(context)
        } else {
            context.getSharedPreferences(
                "${context.packageName}.${namespace}",
                Context.MODE_PRIVATE
            )
        }
    }

    @SuppressLint("ApplySharedPref")
    fun SharedPreferences.verify(): Boolean {
        return try {
            val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = pInfo.versionName
            val editor = edit()
            editor.putString("verify", version)
            editor.commit()
        } catch (e: Exception) {
            false
        }
    }

    private fun createEncryptedNamespace(namespace: String): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context, "${context.packageName}.$namespace", masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun deleteNamespace(namespace: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.deleteSharedPreferences("${context.packageName}.$namespace")
        } else {
            context.filesDir.parent?.let {
                File(
                    "$it/shared_prefs/",
                    "${context.packageName}.$namespace"
                )
            }
        }
    }

    companion object {
        const val COMMON_NAMESPACE = "COMMON_NAMESPACE"
        const val PROTECTED_NAMESPACE = "PROTECTED_NAMESPACE"


        private val protectedNamespace = hashSetOf(PROTECTED_NAMESPACE)
        private val masterKey by lazy {
            MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }

        private val prefsMap: HashMap<String, PrefsContainer> = HashMap()
        private val encryptedPrefsMap: HashMap<String, EncryptedPrefsContainer> = HashMap()

        private var context: Context by Delegates.notNull()

        val version: String by lazy {
            try {
                val pInfo: PackageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    0
                )
                pInfo.versionName ?: ""
            } catch (e: Exception) {
                ""
            }
        }

        fun init(context: Context) {
            Companion.context = context
        }

        fun protectedNamespace(namespace: String) {
            protectedNamespace.add(namespace)
        }

        fun unprotectNamespace(namespace: String) {
            if (namespace != PROTECTED_NAMESPACE) protectedNamespace.remove(namespace)
        }

        fun clear(namespace: String? = null) {
            if (!namespace.isNullOrBlank()) {
                prefsMap[namespace]?.clear()
                encryptedPrefsMap[namespace]?.clear()
            } else {
                prefsMap.filterKeys { it !in protectedNamespace }
                    .forEach { it.value.clear() }
                encryptedPrefsMap.filterKeys { it !in protectedNamespace }
                    .forEach { it.value.clear() }
            }
        }
    }
}

sealed class AbstractPrefsContainer(val namespace: String) : PrefsManager() {
    abstract val preferenceManager: SharedPreferences

    @SuppressLint("ApplySharedPref")
    fun clear() {
        try {
            preferenceManager.edit().clear().commit()
        } catch (e: Exception) {
            getAsUnencrypted(namespace).edit().clear().commit()
        }
    }

    @SuppressLint("ApplySharedPref")
    fun verify(): Boolean {
        return try {
            val editor = preferenceManager.edit()
            editor.putString("verify", version)
            editor.commit()
        } catch (e: Exception) {
            false
        }
    }

    protected inner class NullableStringPref(
        private val key: String? = null,
        private val transformer: PreferenceTransformer<String>? = null,
        private val onChange: ((String?) -> Unit)? = null,
    ) : ReadWriteProperty<Any, String?> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any, property: KProperty<*>): String? {
            val name = key ?: property.name
            return if (transformer != null) {
                transformer.decode(preferenceManager.getString(name, null))
            } else {
                preferenceManager.getString(name, null)
            }
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
            val name = property.name
            val edit = preferenceManager.edit()

            if (transformer != null) {
                edit.putString(name, transformer.encode(value))
            } else {
                edit.putString(name, value)
            }

            edit.apply()
            onChange?.invoke(value)
        }
    }

    protected inline fun <reified T : Enum<T>> PrefsContainer.EnumPref(
        defaultValue: T,
        key: String? = null,
        customPrefix: String? = null,
        transformer: PreferenceTransformer<String>? = null,
        noinline onChange: ((T) -> Unit)? = null,
    ) =
        object : DelegateProvider<ReadWriteProperty<Any, T>> {
            override fun provideDelegate(thisRef: Any?, property: KProperty<*>) =
                object : ReadWriteProperty<Any, T> {
                    private val prefName = key ?: property.defaultDelegateName(customPrefix)

                    override fun getValue(thisRef: Any, property: KProperty<*>): T {

                        val prefName = key ?: property.name

                        val name: String? = if (transformer != null) {
                            transformer.decode(
                                preferenceManager.getString(
                                    prefName,
                                    defaultValue.name
                                )
                            )
                        } else {
                            preferenceManager.getString(prefName, defaultValue.name)
                        }

                        return try {
                            name?.let { enumValueOf<T>(name) } ?: defaultValue
                        } catch (_: Exception) {
                            defaultValue
                        }

                    }

                    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {

                        val edit = preferenceManager.edit()

                        if (transformer != null) {
                            edit.putString(prefName, transformer.encode(value.name))
                        } else {
                            edit.putString(prefName, value.name)
                        }

                        edit.apply()
                        onChange?.invoke(value)
                    }
                }
        }

    protected open inner class SharedStringSet(
        private val defaultValue: Set<String>? = null,
        private val key: String? = null,
        private val onChange: ((Set<String>) -> Unit)? = null,
    ) : ReadWriteProperty<Any, Set<String>> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any, property: KProperty<*>): Set<String> {

            val name = key ?: property.name
            return preferenceManager.getStringSet(name, defaultValue) ?: setOf()

        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Set<String>) {

            val name = key ?: property.name

            val edit = preferenceManager.edit()
            edit.putStringSet(name, value)

            edit.apply()
            onChange?.invoke(value)

        }
    }

    protected open inner class SharedPref<T>(
        private val defaultValue: T,
        private val key: String? = null,
        private val transformer: PreferenceTransformer<T>? = null,
        private val onChange: ((T) -> Unit)? = null,
    ) : ReadWriteProperty<Any, T> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any, property: KProperty<*>): T {

            val name = key ?: property.name
            return if (transformer != null) transformer.decode(
                preferenceManager.getString(
                    name,
                    null
                )
            ) ?: defaultValue
            else when (defaultValue) {
                is Boolean -> preferenceManager.getBoolean(name, defaultValue) as T
                is Float -> preferenceManager.getFloat(name, defaultValue) as T
                is Int -> preferenceManager.getInt(name, defaultValue) as T
                is Long -> preferenceManager.getLong(name, defaultValue) as T
                is String -> preferenceManager.getString(name, defaultValue) as T
                else -> throw UnsupportedOperationException("Unsupported preference type ${property.javaClass} on property $name")
            }
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            val name = key ?: property.name

            val edit = preferenceManager.edit()

            if (transformer != null) edit.putString(name, transformer.encode(value))
            else when (defaultValue) {
                is Boolean -> edit.putBoolean(name, value as Boolean)
                is Float -> edit.putFloat(name, value as Float)
                is Int -> edit.putInt(name, value as Int)
                is Long -> edit.putLong(name, value as Long)
                is String -> edit.putString(name, value as String)
                else -> throw UnsupportedOperationException("Unsupported preference type ${property.javaClass} on property $name")
            }

            edit.apply()
            onChange?.invoke(value)
        }
    }
}

abstract class PrefsContainer(namespace: String, val privacy: Int = Context.MODE_PRIVATE) :
    AbstractPrefsContainer(namespace) {
    init {
        addManagedContainer(this)
    }

    override val preferenceManager: SharedPreferences by lazy {
        getManagedContainer(namespace)
    }
}

abstract class EncryptedPrefsContainer(namespace: String) : AbstractPrefsContainer(namespace) {
    init {
        addEncryptedManagedContainer(this)
    }

    override val preferenceManager: SharedPreferences by lazy {
        getEncryptedMangedContainer(namespace)
    }
}

interface DelegateProvider<out T> {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): T
}

inline fun KProperty<*>.defaultDelegateName(customPrefix: String?, separator: String = "::") =
    (customPrefix
        ?: if (this is KClass<*>) this.java.canonicalName else null)?.let { it + separator + name }
        ?: name

abstract class PreferenceTransformer<T> {
    abstract fun encode(value: T?): String?
    abstract fun decode(value: String?): T?
}