import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    pivot.`base-conventions`
    pivot.`shadow-conventions`
    pivot.`publish-conventions`
}

dependencies {
    api(project(":core"))
    api(project(":bungee"))
    api(project(":spigot"))
    api(project(":velocity"))

}

publishing.publications.create<MavenPublication>("maven") {

    artifactId = "Pivot"
    version = rootProject.version.toString()
    group = rootProject.group.toString()

    artifact(tasks.named<ShadowJar>("shadowJar"))
}


publishing.repositories {
    maven {

        url = uri(correctPublishUrl(findProperty("NEXUS_URL") as String))
        credentials {
            username = (findProperty("NEXUS_REPO_USERNAME") ?: "") as String
            password = (findProperty("NEXUS_REPO_PASSWORD") ?: "") as String
        }
    }
}
