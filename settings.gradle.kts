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
    id("com.gradle.develocity") version("3.17.6")
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.12.1"
}

val isCI = System.getenv("CI") != null

develocity {
    allowUntrustedServer = true // ensure a trusted certificate is configured

    server = "https://ge.solutions-team.gradle.com/" // adjust to your Develocity server

    buildScan {
        uploadInBackground = !isCI
    }
}

buildCache {
    local {
        isEnabled = true
    }

    remote(develocity.buildCache) {
        isEnabled = true
        isPush = false
    }
}

rootProject.name = "kotlinTests"

