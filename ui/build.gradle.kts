plugins {
    id("com.android.library")

    id("kotlin-android")
    id("kotlin-kapt")

    id("androidx.navigation.safeargs")
}

android {
    compileSdkVersion(BuildAndroidConfig.COMPILE_SDK_VERSION)

    defaultConfig {
        minSdk = (BuildAndroidConfig.MIN_SDK_VERSION)
        targetSdk = (BuildAndroidConfig.TARGET_SDK_VERSION)

        testInstrumentationRunner = BuildAndroidConfig.TEST_INSTRUMENTATION_RUNNER
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lintOptions {
        // TODO :: this is a bug in lint
        disable("InvalidFragmentVersionForActivityResult")
        disable("UnsafeExperimentalUsageError", "UnsafeExperimentalUsageWarning")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":resources"))

    applyBase()
    applyFragments()
    applyNavigation()

    applyPaging()
    applyUI()
    applyCoroutines()
    applyDialogs()
    applyKoin()
    applyLifecycle()
}