import java.net.URI

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka") version "1.9.10"
    `maven-publish`
    `java-library`
    common
}

repositories {
    mavenCentral()
}

group = "no.nav.dagpenger"

dependencies {
    implementation(libs.bundles.jackson)
    implementation(libs.rapids.and.rivers)
    implementation(libs.kotlin.logging)
    implementation(libs.konfig)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.json)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

tasks.named("build") {
    dependsOn("sourcesJar")
}

publishing {
    publications {
        create<MavenPublication>("name") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/navikt/dp-aktivitetslogg-bibliotek")
            credentials {
                val githubUser: String? by project
                val githubPassword: String? by project
                username = githubUser
                password = githubPassword
            }
        }
    }
}
