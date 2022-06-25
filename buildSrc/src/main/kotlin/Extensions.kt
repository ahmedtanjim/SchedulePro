import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.add
import java.util.*

fun DependencyHandler.kapt(dependencyNotation: String) = add("kapt", dependencyNotation)
fun DependencyHandler.implementation(dependencyNotation: String) = add("implementation", dependencyNotation)

fun DependencyHandler.implementation(dependencyNotation: String, dependencyConfig: ExternalModuleDependency.()->Unit) =
    add("implementation", dependencyNotation, dependencyConfig)

fun DependencyHandler.testImplementation(dependencyNotation: String) = add("testImplementation", dependencyNotation)
fun DependencyHandler.androidTestImplementation(dependencyNotation: String) = add("androidTestImplementation", dependencyNotation)

fun Project.resolveProperty(propertyName: String): String {
    val envKey = propertyName.replace(".", "_").toUpperCase(Locale.getDefault())
    if (System.getenv().containsKey(envKey)) {
        return System.getenv(envKey)
    }
    return if (hasProperty(propertyName)) {
        property(propertyName) as String
    } else {
        ""
    }
}

fun Project.loadProperties(filePath: String): Properties {
    val props = Properties()
    file(filePath).inputStream().let {
        props.load(it)
    }
    return props
}