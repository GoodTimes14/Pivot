import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    id("com.gradleup.shadow")
}


tasks.named<ShadowJar>("shadowJar") {

    //minimize()
    archiveFileName.set("${rootProject.name}-${project.name}-${rootProject.version}.jar")

    relocate("com.zaxxer", "eu.magicmine.pivot.libs.com.zaxxer")
    relocate("com.mysql", "eu.magicmine.pivot.libs.com.mysql")

    relocate("org.mariadb", "eu.magicmine.pivot.libs.org.mariadb")
    relocate("io.lettuce", "eu.magicmine.pivot.libs.io.lettuce")
    relocate("io.netty", "eu.magicmine.pivot.libs.io.netty")
    relocate("org.apache", "eu.magicmine.pivot.libs.org.apache")
    relocate("org.slf4j", "eu.magicmine.pivot.libs.org.slf4j")
    relocate("org.reactivestreams", "eu.magicmine.pivot.libs.org.reactivestreams")
    relocate("reactor", "eu.magicmine.pivot.libs.reactor")
    
    mergeServiceFiles()
}

tasks.named("assemble") {
    dependsOn(tasks.named("shadowJar"))
    
    
}
