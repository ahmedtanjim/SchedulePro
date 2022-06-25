rootProject.name = "SchedulePro"
rootProject.buildFileName = "build.gradle.kts"

plugins {
    id("com.gradle.enterprise") version "3.3.4"
}

gradleEnterprise {
    buildScan {
// Accept the license agreement for com.gradle.build-scan plugin
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishOnFailure()
    }
}

include(":app", ":core", ":resources", ":ui")
