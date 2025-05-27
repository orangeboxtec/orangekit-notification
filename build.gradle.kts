import org.kordamp.gradle.plugin.jandex.tasks.JandexTask

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.allopen") version "2.0.21"
    id("io.quarkus")
    id("maven-publish")
    id("org.kordamp.gradle.jandex") version "2.1.0"
}

group = "com.orangebox.kit.notification"
version = "2.1.6"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://artifactory.startup-kit.net/artifactory/orangekit")
    }
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation("io.quarkus:quarkus-rest-jsonb")
    implementation(platform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("com.mailjet:mailjet-client:4.5.0")
    implementation("com.twilio.sdk:twilio:9.2.5")
    implementation("commons-io:commons-io")
    implementation("commons-codec:commons-codec")
    implementation("io.quarkus:quarkus-mongodb-client")
    implementation("io.quarkus:quarkus-websockets")

    implementation("com.orangebox.kit.core:orangekit-core:2.1.0")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<JandexTask> {
    dependsOn(":quarkusDependenciesBuild")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "orangebox"
            url = uri("https://artifactory.startup-kit.net/artifactory/orangekit")
            credentials(PasswordCredentials::class)
        }
    }
}
