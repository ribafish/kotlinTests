pluginManagement {
    repositories {
        maven {
            url = uri("https://plugins.grdev.net/m2")
        }
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.16.2"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.13"
}

gradleEnterprise {
    allowUntrustedServer = false // ensure a trusted certificate is configured

    server = "https://ge.solutions-team.gradle.com/" // adjust to your Develocity server

    buildScan {
        capture { isTaskInputFiles = true }
        publishAlways()
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

