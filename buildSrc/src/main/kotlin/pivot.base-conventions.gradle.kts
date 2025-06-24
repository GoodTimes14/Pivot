plugins {
    `java-library`
    id("io.freefair.lombok")
    id("com.diffplug.spotless")
}

group = rootProject.group
version = rootProject.version
description = rootProject.description

// Java compilation settings
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    disableAutoTargetJvm()
    withSourcesJar()
}


spotless {
    java {
        endWithNewline()
        leadingTabsToSpaces(4)
        removeUnusedImports()
        trimTrailingWhitespace()
        targetExclude("build/generated/**/*")
    }

    kotlinGradle {
        endWithNewline()
        leadingTabsToSpaces(4)
        trimTrailingWhitespace()
    }
}


tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    build {
        // Ensure spotlessApply runs before build
        dependsOn(tasks.named("spotlessApply"))
    }
}


defaultTasks("build")
