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
    id("com.gradle.develocity") version("3.18.1")
    id("com.gradle.common-custom-user-data-gradle-plugin") version "2.0.2"
}

val isCI = System.getenv("CI") != null

develocity {
    server = "https://pov-wmckh7flkwepm.develocity.cloud/"

    buildScan {
        uploadInBackground = !isCI
        tag("testingPublishing")
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

