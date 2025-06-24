plugins {
    id("java")
}

group = "it.pivot"
version = "2.0.0"
description = "Utility framework for minecraft servers and proxies"

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.isFork = true
        options.isIncremental = true
    }

    repositories {
        mavenCentral()

        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}