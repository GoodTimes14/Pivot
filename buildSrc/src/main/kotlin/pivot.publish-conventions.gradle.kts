import java.io.ByteArrayOutputStream


plugins {
    `maven-publish`
}

fun getGitBranch(): String {
    val stdout = ByteArrayOutputStream()


    providers.exec {
        commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
        standardOutput = stdout
    }

    return stdout.toString().trim()
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()

    providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }

    return stdout.toString().trim()
}

fun isSnapshot(): Boolean {
    val currentBranch = getGitBranch()
    return currentBranch != "2.0-REBORN";
}


fun correctVersion(rootVersion: String? = null): String? {
    val suffix: String = if (isSnapshot()) "-SNAPSHOT" else ""
    val currentVersion: String = rootVersion ?: rootProject.version as String;

    println("snapshot: " + isSnapshot())
    return if(currentVersion.endsWith(suffix)) currentVersion else currentVersion + suffix
}

fun correctPublishUrl(baseUrl: String): String {
    return baseUrl + (if (isSnapshot())  "maven-snapshots" else "maven-releases") + "/"
}
