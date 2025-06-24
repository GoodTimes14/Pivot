plugins {
    pivot.`base-conventions`
    pivot.`shadow-conventions`
}


repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
    compileOnly(project(":core"))
}
