// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}

allprojects {
    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.apache.commons" && requested.name == "commons-compress") {
                useVersion("1.26.2")
                because("Fix ZipArchiveOutputStream.putArchiveEntry NoSuchMethodError JReleaser error.")
            }
        }
    }
}

buildscript {
    configurations.classpath {
        resolutionStrategy.force("org.apache.commons:commons-compress:1.26.2")
    }
}
