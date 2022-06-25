plugins {
    id("com.android.library")

    id("kotlin-android")
    id("kotlin-kapt")

    id("androidx.navigation.safeargs")
    id("com.google.protobuf") version Libs.PROTOBUFF_PLUGIN_VERSION
}

android {
    compileSdk = BuildAndroidConfig.COMPILE_SDK_VERSION
    defaultConfig {
        minSdk = BuildAndroidConfig.MIN_SDK_VERSION
        targetSdk = BuildAndroidConfig.TARGET_SDK_VERSION

        testInstrumentationRunner = BuildAndroidConfig.TEST_INSTRUMENTATION_RUNNER

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }

        buildConfigField("String", "QA_ENV_API_KEY", "\"${resolveProperty("schedulepro.qa_api_key")}\"")
        buildConfigField("String", "QA_ENV_CLIENT_ID", "\"${resolveProperty("schedulepro.qa_client_id")}\"")

        buildConfigField("String", "DEV_ENV_API_KEY", "\"${resolveProperty("schedulepro.debug_api_key")}\"")
        buildConfigField("String", "DEV_ENV_CLIENT_ID", "\"${resolveProperty("schedulepro.debug_client_id")}\"")

        buildConfigField("String", "PROD_ENV_API_KEY", "\"${resolveProperty("schedulepro.release_api_key")}\"")
        buildConfigField("String", "PROD_ENV_CLIENT_ID", "\"${resolveProperty("schedulepro.release_client_id")}\"")
    }

    buildTypes {
        fun filesInPath(path: String): Array<String> {
            return File(path).list() ?: arrayOf()
        }
        getByName("release") {
            isMinifyEnabled = true
            consumerProguardFiles(
                "proguard-rules.pro",
                "$buildDir/intermediates/proguard-rules/release/aapt_rules.txt",
                "$buildDir/intermediates/apt_proguard_file/release/aapt_rules.txt",
                *filesInPath("$buildDir/intermediates/proguard-files")
            )
        }
        getByName("debug") {

        }
        create("qa") {
            isMinifyEnabled = true

            consumerProguardFiles(
                "proguard-rules.pro",
                "$buildDir/intermediates/proguard-rules/release/aapt_rules.txt",
                "$buildDir/intermediates/apt_proguard_file/release/aapt_rules.txt",
                *filesInPath("$buildDir/intermediates/proguard-files")
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    sourceSets {
        getByName("androidTest") {
            assets {
                srcDirs("$projectDir/schemas")
            }
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(project(":resources"))

    applyBase()
    applyFragments()
    applyFirebase()

    implementation(Libs.ANDROIDX_PREFERENCE)
    implementation(Libs.OPEN_ID)

    applyDatastore()
    applyPaging()
    applyPlayServices()
    applyNavigation()
    applyUI()
    applyCrypto()
    applyCoroutines()
    applyDialogs()
    applyKoin()
    applyLifecycle()
    applyRetrofit()
    applyMoshi()
    applyRoom()

    applyTests()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=androidx.paging.ExperimentalPagingApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.contracts.ExperimentalContracts"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
}

// Temporary fix for gradle 6.8 kotlin dsl issue optimally we want to use the commented out code,
// but it currently doesn't work with the current plugin version

apply(from = "protobuf.gradle")

//protobuf {
//    protoc {
//        artifact = Libs.PROTOC
//    }
//    generateProtoTasks {
//        all().forEach { task ->
//            task.builtins {
//                id("java") {
//                    option("lite")
//                }
//            }
//        }
//    }
//}