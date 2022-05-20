pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "KMMCoordinatorExample"


include(":library")
include(":android")

