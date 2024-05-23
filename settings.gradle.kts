pluginManagement {
    repositories {
//        maven {
//            url = uri("https://plugins.grdev.net/m2")
//        }
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    id("com.gradle.develocity") version("3.17.4")
    id("com.gradle.common-custom-user-data-gradle-plugin") version "2.0.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

val isCI = System.getenv("CI") != null
val exp="cache-exp-2"

develocity {
    allowUntrustedServer = true // ensure a trusted certificate is configured

//    server = "https://ge.solutions-team.gradle.com/" // adjust to your Develocity server
//    server = "https://develocity-04b8b6f9.nip.io" // adjust to your Develocity server
    server = "https://develocity-3308e4bb.nip.io" // adjust to your Develocity server

    buildScan {
        uploadInBackground = !isCI
        tag(exp)
    }
}

buildCache {
    local {
        isEnabled = false
    }

    remote(develocity.buildCache) {
        isEnabled = true
        isPush = true
        path = "cache/$exp"
    }
}

rootProject.name = "kotlinTests"

