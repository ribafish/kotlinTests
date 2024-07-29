import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0-Beta2"
    kotlin("plugin.serialization") version "2.0.0-Beta2"
    application
    id("com.github.ben-manes.versions") version "0.50.0"
    id("groovy")
}

group = "me.gasperkojek"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0-Beta2")

    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")

    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("org.codehaus.groovy:groovy-all:3.0.21")
    implementation("org.codehaus.groovy:groovy-xml:3.0.21")

    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}

tasks.test {
    useJUnitPlatform()
    systemProperties(mapOf(
        "https.proxyHost" to "localhost",
        "https.proxyPort" to "1080"
    ))
}

val jvmVersion = 17

kotlin {
    jvmToolchain(jvmVersion)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmVersion))
    }
}

application {
    mainClass.set("MainKt")
}

// badConfig stuff is here to get a successful build with failed to resolve dependencies
val badConfig by configurations.creating
val badConfig2 by configurations.creating {
    extendsFrom(configurations.compileClasspath.get())
}

dependencies {
    badConfig("org.fake:does-not-exist:1")
    badConfig2("org.fake:does-not-exist:2")
}

tasks.register("badConfigTask") {
    doLast {
        try {
            badConfig.resolve().toString()
        } catch (e: Exception) {
            e.message
        }
        badConfig2.incoming.artifactView {
            lenient(true)
        }.files.files
        configurations.compileClasspath.get().resolve()
    }
}
