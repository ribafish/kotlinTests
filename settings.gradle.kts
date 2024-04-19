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
    id("com.gradle.develocity") version("3.17.2")
    id("com.gradle.common-custom-user-data-gradle-plugin") version "2.0.1"
}

val isCI = System.getenv("CI") != null

gradleEnterprise {
    allowUntrustedServer = false // ensure a trusted certificate is configured

    server = "https://ge.solutions-team.gradle.com/" // adjust to your Develocity server

    buildScan {
        capture { isTaskInputFiles = true }
        publishAlways()
        isUploadInBackground = !isCI
    }
}

buildCache {
    local {
        isEnabled = true
    }

    remote(gradleEnterprise.buildCache) {
        isEnabled = true
        isPush = false
    }
}

rootProject.name = "kotlinTests"

