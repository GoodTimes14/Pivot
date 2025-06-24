plugins {
    pivot.`base-conventions`
    pivot.`shadow-conventions`
}


repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.0")
    annotationProcessor("org.jetbrains:annotations:24.0.0")
    compileOnly(libs.spigot)
    compileOnly(project(":core"))
}
