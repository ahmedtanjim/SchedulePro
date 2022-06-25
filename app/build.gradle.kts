import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")

    id("kotlin-android")
    id("kotlin-kapt")

    // perf plugin is fiddly about order
    id("com.google.firebase.firebase-perf")
    id("androidx.navigation.safeargs")

    id("com.google.firebase.crashlytics")
    id("com.google.firebase.appdistribution")

    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.gms.google-services")
}

android {

    compileSdk = 31
    val baseVersionName = "1.7"

    defaultConfig {
        manifestPlaceholders += mapOf()
        multiDexEnabled = true
        applicationId = "com.shiftboard.schedulepro"

        minSdk = 23
        targetSdk = 31

        versionCode = 1
        versionName = "$baseVersionName.$versionCode"

        manifestPlaceholders["appLabel"] = "@string/app_name"

        buildConfigField("String", "BRANCH_NAME", "\"\"")

        testInstrumentationRunner = BuildAndroidConfig.TEST_INSTRUMENTATION_RUNNER
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("debug")
    }

    signingConfigs {
        create("dev") {
            storeFile = file("../debug.keystore")
            keyAlias = "schedulepro"
            keyPassword = "schedulepro"
            storePassword = "schedulepro"
        }
        create("qa") {
            storeFile = file(resolveProperty("schedulepro.qa_keystore"))
            keyAlias = resolveProperty("schedulepro.qa_key_alias")
            keyPassword = resolveProperty("schedulepro.qa_key_password")
            storePassword = resolveProperty("schedulepro.qa_key_storepass")
        }
    }

    buildTypes {
        fun filesInPath(path: String): Array<String> {
            return File(path).list() ?: arrayOf()
        }
        getByName("release") {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "$buildDir/intermediates/proguard-rules/release/aapt_rules.txt",
                "$buildDir/intermediates/apt_proguard_file/release/aapt_rules.txt",
                *filesInPath("$buildDir/intermediates/proguard-files")
            )
        }
        getByName("debug") {
            val file = File(rootProject.projectDir, "debug_version.properties")
            println(file)
            if (file.exists()) {
                val props = Properties()
                file.inputStream().let {
                    props.load(it)
                }
                buildConfigField("String", "BRANCH_NAME", "\"${props["debug_version_name"]}\"")
            }

            isDebuggable = true
            isMinifyEnabled = false

            manifestPlaceholders["appLabel"] = "@string/app_name_dev"
            signingConfig = signingConfigs.findByName("dev")

            applicationIdSuffix = ".dev"
        }
        create("qa") {
            isMinifyEnabled = false
            isDebuggable = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "$buildDir/intermediates/proguard-rules/qa/aapt_rules.txt",
                "$buildDir/intermediates/apt_proguard_file/qa/aapt_rules.txt",
                *filesInPath("$buildDir/intermediates/proguard-files")
            )

            signingConfig = signingConfigs.findByName("qa")

            applicationIdSuffix = ".qa"
            manifestPlaceholders["appLabel"] = "@string/app_name_qa"

            firebaseAppDistribution {
                serviceCredentialsFile = resolveProperty("schedulepro.dist_service_creds")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion ="1.0.1"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":resources"))

    implementation (Libs.COMPOSE.RUNTIME)
    implementation (Libs.COMPOSE.UI)
    implementation (Libs.COMPOSE.FOUNDATION)
    implementation (Libs.COMPOSE.LAYOUT)
    implementation (Libs.COMPOSE.MATERIAL)
    implementation (Libs.COMPOSE.LIVE_DATA)
    implementation (Libs.COMPOSE.X_UI)
    implementation (Libs.COMPOSE.THEME)
    implementation (Libs.COMPOSE.ACTIVITY)
    implementation (Libs.COMPOSE.VIEW_MODEL)
    implementation (Libs.COMPOSE.FLOW)
    applyBase()
    applyFragments()
    applyPlayServices()
    applyNavigation()

    implementation(Libs.ANDROIDX_PREFERENCE)
    implementation(Libs.OPEN_ID)

    applyPaging()
    applyUI()
    applyCrypto()
    applyCoroutines()
    applyDialogs()
    applyKoin()
    applyLifecycle()
    applyRetrofit()
    applyMoshi()
    applyRoom()
    applyFirebase()

    applyTests()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-Xinline-classes"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
}

tasks.register("createVersionName", VersionNameTask::class)

afterEvaluate {
    tasks.findByName("preDebugBuild")?.dependsOn("createVersionName")
}