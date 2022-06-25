plugins.apply("plugins.update-dependencies")

buildscript {

    repositories {
        google()
        jcenter()
        maven(uri("https://oss.sonatype.org/content/repositories/snapshots"))
        maven(uri("https://jitpack.io"))
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath(Libs.FIREBASE_DISTRIBUTION)
        classpath(Libs.GOOGLE_PLAY)
        classpath(Libs.FIREBASE_PERFORMANCE)
        classpath(Libs.FIREBASE_CRASH)
        classpath(Libs.NAVIGATION_SAFEARGS)
        classpath(Libs.GOOGLE_LICENSES)

    }
}

allprojects {

    repositories {
        google()
        jcenter()
        maven(uri("https://dl.bintray.com/kotlin/kotlin-eap"))
        maven(uri("https://kotlin.bintray.com/kotlinx"))
        maven(uri("https://oss.sonatype.org/content/repositories/snapshots"))
        maven(uri("https://jitpack.io"))
    }

    configurations.all {
        resolutionStrategy {
            force(forcedLibraries())
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    delete(File(rootProject.projectDir, "buildSrc/build"))
    delete(File(rootProject.projectDir, "build"))
}