plugins {
    pivot.`base-conventions`
    pivot.`shadow-conventions`
}


dependencies {

    implementation("io.lettuce:lettuce-core:6.5.3.RELEASE")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.apache.commons:commons-pool2:2.12.0")
    implementation("com.mysql:mysql-connector-j:9.2.0")
}
