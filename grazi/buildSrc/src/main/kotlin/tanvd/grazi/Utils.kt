package tanvd.grazi

import org.gradle.api.Project

fun Project.gitBranch(): String {
    val builder = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
    builder.directory(rootProject.projectDir)
    val proc = builder.start()
    val branch = proc.inputStream.bufferedReader().readText()
    return branch.split("\n").single { it.isNotBlank() }
}

val Project.channel: String
    get() {
        val branch = gitBranch()
        if (branch == "master") {
            return "stable"
        }
        return branch
    }
