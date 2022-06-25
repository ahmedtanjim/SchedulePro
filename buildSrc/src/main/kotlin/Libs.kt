@file:Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate")

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude

object Libs {
    const val compose_version = "1.1.0"
    // support for crypto preferences
    const val ANDROIDX_PREFERENCE = "androidx.preference:preference-ktx:1.2.0"

    // Additional features for activities
    const val ACTIVITY = "androidx.activity:activity:1.4.0"

    // Kotlin compatibility for androidx.activity
    const val ACTIVITY_KTX = "androidx.activity:activity-ktx:1.4.0"

    const val ANDROID_JUNIT = "androidx.test.ext:junit:1.1.3"
    const val ANDROID_ANNOTATIONS = "androidx.annotation:annotation:1.3.0"

    // Backports for android functionality
    const val APPCOMPAT = "androidx.appcompat:appcompat:1.4.1"

    // Adding in keystore crypto for preferences, sql and protostore
    const val CRYPTO = "androidx.security:security-crypto:1.1.0-alpha03"

    // Kotlin async library
    const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0"

    // Kotlin async library
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0"



    // support for constraint layouts
    const val CONSTRAINTS = "androidx.constraintlayout:constraintlayout:2.1.3"

    // Compatibility for platform features without causing issues on older APIs
    const val CORE_KTX = "androidx.core:core-ktx:1.7.0"

    // Async typesafe upgrade to preferences
    const val DATASTORE_CORE = "androidx.datastore:datastore-core:1.0.0-alpha02"

    // Goto material dialogs library
    const val DIALOGS_CORE = "com.afollestad.material-dialogs:core:3.3.0"
    const val DIALOGS_INPUT = "com.afollestad.material-dialogs:input:3.3.0"
    const val DIALOGS_DATETIME = "com.afollestad.material-dialogs:datetime:3.3.0"
    const val DIALOGS_LIFECYCLE = "com.afollestad.material-dialogs:lifecycle:3.3.0"
    const val DIALOGS_BOTTOMSHEET = "com.afollestad.material-dialogs:bottomsheets:3.3.0"

    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.4.0"

    // Bill of materials build of latest firebase libraries
    const val FIREBASE_BOM = "com.google.firebase:firebase-bom:29.1.0"

    // FIREBASE_BOM - Do not version

    // Support for analytics
    const val FIREBASE_ANALYTICS = "com.google.firebase:firebase-analytics-ktx"

    // Support for crash reporting
    const val FIREBASE_CRASHLYTICS = "com.google.firebase:firebase-crashlytics-ktx"

    // Support for FCM
    const val FIREBASE_MESSAGING = "com.google.firebase:firebase-messaging-ktx"

    // Support for performance monitoring
    const val FIREBASE_PERF = "com.google.firebase:firebase-perf-ktx"

    // FIREBASE_BOM

    const val FIREBASE_DISTRIBUTION = "com.google.firebase:firebase-appdistribution-gradle:3.0.0"
    const val FIREBASE_PERFORMANCE = "com.google.firebase:perf-plugin:1.4.1"
    const val FIREBASE_CRASH = "com.google.firebase:firebase-crashlytics-gradle:2.8.1"

    const val FLOW_BINDING = "io.github.reactivecircus.flowbinding:flowbinding-android:1.2.0"
    const val FLOW_EXT = "com.github.akarnokd:kotlin-flow-extensions:0.0.14"

    const val FRAGMENT = "androidx.fragment:fragment:1.4.1"
    const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:1.4.1"
    const val FLEX_BOX = "com.google.android:flexbox:2.0.1"

    const val GOOGLE_PLAY = "com.google.gms:google-services:4.3.10"
    const val GOOGLE_LICENSES = "com.google.android.gms:oss-licenses-plugin:0.10.4"

    const val JUNIT = "junit:junit:4.13.1"

    // Dependency Injection Framework (preference over dagger)
    const val KOIN = "org.koin:koin-android:2.2.0-beta-1"
    const val KOIN_SCOPE = "org.koin:koin-androidx-scope:2.2.0-beta-1"
    const val KOIN_FRAGMENT = "org.koin:koin-androidx-fragment:2.2.0-beta-1"
    const val KOIN_EXT = "org.koin:koin-androidx-ext:2.2.0-beta-1"
    const val KOIN_VIEWMODEL = "org.koin:koin-androidx-viewmodel:2.2.0-beta-1"

    // Kotlin standard libraries
    const val KOTLIN_STD_JDK8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21"
    const val KOTLIN_STD_JDK7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.5.21"
    const val KOTLIN_STD = "org.jetbrains.kotlin:kotlin-stdlib:1.5.21"
    const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:1.5.21"

    // Lifecycle management for different android components
    const val LIFECYCLE_VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1"
    const val LIFECYCLE_VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:2.4.1"
    const val LIFECYCLE_RUNTIME = "androidx.lifecycle:lifecycle-runtime-ktx:2.4.1"
    const val LIFECYCLE_SAVESTATE = "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.4.1"
    const val LIFECYCLE_COMPILER = "androidx.lifecycle:lifecycle-compiler:2.4.1"



    // Material design components
    const val MATERIAL = "com.google.android.material:material:1.3.0-alpha03"

    // Json parsing framework
    const val MOSHI = "com.squareup.moshi:moshi:1.12.0"

    // Codegen backend for moshi - fastest option for kotlin
    const val MOSHI_CODEGEN = "com.squareup.moshi:moshi-kotlin-codegen:1.12.0"

    // Additional adapters for moshi
    const val MOSHI_ADAPTERS = "com.squareup.moshi:moshi-adapters:1.12.0"

    // Additional adapters for moshi
    const val MOSHI_LAZY_ADAPTERS = "com.serjltt.moshi:moshi-lazy-adapters:2.2"

    // Navigation framework
    const val NAVIGATION_SAFEARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:2.3.2"
    const val NAVIGATION_FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:2.3.2"
    const val NAVIGATION_UI_KTX = "androidx.navigation:navigation-ui-ktx:2.3.2"
    const val NAVIGATION_COMPOSE = "androidx.navigation:navigation-compose:2.4.2"

    // OAuth library
    const val OPEN_ID = "net.openid:appauth:0.11.1"

    // Networking framework
    const val OKHTTP = "com.squareup.okhttp3:okhttp:4.9.3"
    const val OKHTTP_LOGGING = "com.squareup.okhttp3:logging-interceptor:4.9.3"

    const val PROTOBUFF_PLUGIN_VERSION = "0.8.12"

    // Pagination
    const val PAGING_RUNTIME = "androidx.paging:paging-runtime:3.1.0"
    const val PAGING_RUNTIME_KTX = "androidx.paging:paging-runtime-ktx:3.1.0"

    // Requirement for play services
    const val PLAY_BASE = "com.google.android.gms:play-services-base:18.0.1"

    // Requirement for play services
    const val PLAY_BASEMENT = "com.google.android.gms:play-services-basement:18.0.0"

    // Adds license summary activity
    const val PLAY_OSS = "com.google.android.gms:play-services-oss-licenses:17.0.0"

    // Framework for handling protobuff calls
    const val PROTOBUFF_JAVALITE = "com.google.protobuf:protobuf-javalite:3.19.4"
    const val PROTOC = "com.google.protobuf:protoc:3.19.4"

    // SQL framework
    const val ROOM_RUNTIME = "androidx.room:room-runtime:2.4.1"
    const val ROOM_KTX = "androidx.room:room-ktx:2.4.1"
    const val ROOM_COMPILER = "androidx.room:room-compiler:2.4.1"
    const val ROOM_TESTING = "androidx.room:room-testing:2.4.1"
    const val ROOM_PAGEING = "androidx.room:room-paging:2.4.1"

    // REST framework
    const val RETROFIT = "com.squareup.retrofit2:retrofit:2.9.0"

    // Moshi compatibility for Retrofit
    const val RETROFIT_MOSHI = "com.squareup.retrofit2:converter-moshi:2.9.0"

    // Advanced memory efficient list views
    const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.2.1"

    // SQL encryption for room
    const val SQL_CIPHER = "net.zetetic:android-database-sqlcipher:4.5.0"

    // replacement backend for room
    const val SQLITE = "androidx.sqlite:sqlite:2.2.0"

    // Temporary in memory state saving for
    const val SAVEDSTATE = "androidx.savedstate:savedstate:1.1.0"

    // Logging framework
    const val TIMBER = "com.jakewharton.timber:timber:5.0.1"

    // Time framework
    const val THREETEN = "com.jakewharton.threetenabp:threetenabp:1.3.1"

    // encryption dependency
    val TINK = "com.google.crypto.tink:tink-android:1.6.1"

    object COMPOSE
    {
        const val RUNTIME = "androidx.compose.runtime:runtime:$compose_version"
        const val UI = "androidx.compose.ui:ui:$compose_version"
        const val FOUNDATION = "androidx.compose.foundation:foundation:$compose_version"
        const val LAYOUT = "androidx.compose.foundation:foundation-layout:$compose_version"
        const val MATERIAL = "androidx.compose.material:material:$compose_version"
        const val LIVE_DATA = "androidx.compose.runtime:runtime-livedata:$compose_version"
        const val X_UI = "androidx.compose.ui:ui-tooling:$compose_version"
        const val THEME = "com.google.android.material:compose-theme-adapter:$compose_version"
        const val ACTIVITY = "androidx.activity:activity-compose:1.3.1"
        const val VIEW_MODEL = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"
        const val FLOW = "com.google.accompanist:accompanist-flowlayout:0.24.3-alpha"
    }


}

fun DependencyHandlerScope.applyPlayServices() {
    dependencies.run {
        implementation(Libs.PLAY_BASE) {
            excludeFragment()
            // Basement also includes a version of the fragments code so we need to also remove the
            // pined version of that as well
            exclude("com.google.android.gms", "play-services-basement")
        }
        implementation(Libs.PLAY_BASEMENT) { excludeFragment() }
        implementation(Libs.PLAY_OSS) { excludeFragment() }
    }
}

fun DependencyHandlerScope.applyDatastore() {
    dependencies.run {
        implementation(Libs.DATASTORE_CORE)
        implementation(Libs.PROTOBUFF_JAVALITE)
    }
}

fun DependencyHandlerScope.applyPaging() {
    dependencies.run {
        implementation(Libs.PAGING_RUNTIME)
        implementation(Libs.PAGING_RUNTIME_KTX)
    }
}

fun DependencyHandlerScope.applyBase() {
    dependencies.run {
        implementation(Libs.KOTLIN_STD_JDK8)
        implementation(Libs.KOTLIN_STD)
        implementation(Libs.KOTLIN_REFLECT)

        implementation(Libs.TIMBER)
        implementation(Libs.THREETEN)

        implementation(Libs.CORE_KTX)
        implementation(Libs.APPCOMPAT)
        implementation(Libs.SAVEDSTATE)
    }
}

fun DependencyHandlerScope.applyFragments() {
    dependencies.run {
        implementation(Libs.ACTIVITY)
        implementation(Libs.ACTIVITY_KTX)

        implementation(Libs.FRAGMENT)
        implementation(Libs.FRAGMENT_KTX)
    }
}

fun DependencyHandlerScope.applyNavigation() {
    dependencies.run {
        implementation(Libs.NAVIGATION_FRAGMENT_KTX) { excludeFragment() }
        implementation(Libs.NAVIGATION_COMPOSE)
        implementation(Libs.NAVIGATION_UI_KTX) {
            excludeFragment()
            // We want to make sure the material library is using the latest version and not pinned
            // to the one included in the UI library
            exclude("com.google.android.material", "material")
        }
    }
}

fun DependencyHandlerScope.applyFirebase() {
    dependencies.run {
        add("implementation", platform(Libs.FIREBASE_BOM))

        implementation(Libs.FIREBASE_CRASHLYTICS)
        implementation(Libs.FIREBASE_ANALYTICS)
        implementation(Libs.FIREBASE_MESSAGING)
        implementation(Libs.FIREBASE_PERF)
    }
}

fun DependencyHandlerScope.applyCrypto() {
    dependencies.run {
        implementation(Libs.CRYPTO) {
            // There are some build errors if you use the pinned version of tink.  The updated
            // version uses updated versions of some libraries and can be properly compiled on the
            // latest version of gradle.
            exclude("com.google.crypto.tink", "tink-android")
        }
        implementation(Libs.TINK)
    }
}

fun DependencyHandlerScope.applyMoshi() {
    dependencies.run {
        implementation(Libs.MOSHI)
        implementation(Libs.MOSHI_ADAPTERS)
        implementation(Libs.MOSHI_LAZY_ADAPTERS)

        kapt(Libs.MOSHI_CODEGEN)
    }
}

fun DependencyHandlerScope.applyRetrofit() {
    dependencies.run {
        implementation(Libs.RETROFIT)
        implementation(Libs.RETROFIT_MOSHI)
        implementation(Libs.OKHTTP)
        implementation(Libs.OKHTTP_LOGGING)
    }
}

fun DependencyHandlerScope.applyDialogs() {
    dependencies.run {
        implementation(Libs.DIALOGS_CORE)
        implementation(Libs.DIALOGS_INPUT)
        implementation(Libs.DIALOGS_DATETIME)
        implementation(Libs.DIALOGS_LIFECYCLE)
        implementation(Libs.DIALOGS_BOTTOMSHEET)
    }
}

fun DependencyHandlerScope.applyLifecycle() {
    dependencies.run {
        implementation(Libs.LIFECYCLE_VIEWMODEL)
        implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
        implementation(Libs.LIFECYCLE_RUNTIME)
        implementation(Libs.LIFECYCLE_SAVESTATE)
        kapt(Libs.LIFECYCLE_COMPILER)
    }
}

fun DependencyHandlerScope.applyKoin() {
    dependencies.run {
        implementation(Libs.KOIN)
        implementation(Libs.KOIN_SCOPE)
        implementation(Libs.KOIN_FRAGMENT)
        implementation(Libs.KOIN_EXT)
        implementation(Libs.KOIN_VIEWMODEL)
    }
}

fun DependencyHandlerScope.applyCoroutines() {
    dependencies.run {
        implementation(Libs.COROUTINES_ANDROID)
        implementation(Libs.COROUTINES_CORE)
        implementation(Libs.FLOW_EXT)
        implementation(Libs.FLOW_BINDING)
    }
}

fun DependencyHandlerScope.applyRoom() {
    dependencies.run {
        implementation(Libs.ROOM_RUNTIME)
        implementation(Libs.ROOM_KTX)
        implementation(Libs.ROOM_PAGEING)

        implementation(Libs.SQL_CIPHER)
        implementation(Libs.SQLITE)

        kapt(Libs.ROOM_COMPILER)
        androidTestImplementation(Libs.ROOM_TESTING)
    }
}

fun DependencyHandlerScope.applyUI() {
    dependencies.run {
        implementation(Libs.CONSTRAINTS)
        implementation(Libs.RECYCLER_VIEW)
        implementation(Libs.FLEX_BOX)
        implementation(Libs.MATERIAL) { excludeFragment() }
    }
}

fun DependencyHandlerScope.applyTests() {
    dependencies.run {
        testImplementation(Libs.JUNIT)
        androidTestImplementation(Libs.ANDROID_JUNIT)
        androidTestImplementation(Libs.ANDROID_ANNOTATIONS)
        androidTestImplementation(Libs.ESPRESSO_CORE)
    }
}

/* We want to exclude the fragment implementation to prevent conflicts.  If you leave multiple
   versions of the fragment lib in the path the navigation ui will not render properly which makes
   it really hard to proprely use it. */
private fun ExternalModuleDependency.excludeFragment() {
    exclude("androidx.fragment", "fragment")
    exclude("androidx.fragment", "fragment-ktx")
}

fun forcedLibraries(): List<String> = listOf(
    Libs.FRAGMENT,
    Libs.FRAGMENT_KTX,
    Libs.CONSTRAINTS,
    Libs.PLAY_BASEMENT,
    Libs.ACTIVITY,
    Libs.ACTIVITY_KTX,
    Libs.JUNIT,
    Libs.MATERIAL,
    Libs.APPCOMPAT,
    Libs.TINK,
    Libs.KOTLIN_STD_JDK8,
    Libs.KOTLIN_STD_JDK7,
    Libs.KOTLIN_STD,
    Libs.KOTLIN_REFLECT
)