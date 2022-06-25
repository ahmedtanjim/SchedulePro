import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException


// This task will generate an incrementing build index so we can pass around debug build and they
// generally install properly on top of each other.
open class VersionNameTask: DefaultTask() {
    @TaskAction
    fun runTask() {
        val branch = ("git rev-parse --abbrev-ref HEAD".runCommand() ?: "").replace("\n","")
        val hash = ("git rev-parse --short HEAD".runCommand() ?: "").replace("\n","")

        val debugFile = File(project.rootProject.projectDir, "debug_version.properties")
        if (!debugFile.exists()) {
            debugFile.createNewFile()
        }

        debugFile.writeText("debug_version_name=$branch($hash)")
    }

    private fun String.runCommand(workingDir: File = File(".")): String? {
        return try {
            val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor()
            proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }
}