plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
    id("signing")
}

android {
    namespace = "gurux.dlms"
    compileSdk = 36

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "org.gurux"
                artifactId = "gurux.dlms.android"
                version = "2.0.16"
                pom {
                    name.set("gurux.dlms.android")
                    description.set(
                        "gurux.dlms.android package is a communication library for DLMS devices. " +
                                "Purpose of Gurux Device Framework is to help you read your devices, meters and sensors easier"
                    )
                    url.set("https://www.gurux.fi")
                    licenses {
                        license {
                            name.set("GNU General Public License, version 2")
                            url.set("http://www.gnu.org/licenses/gpl-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("gurux")
                            name.set("Gurux ltd")
                            email.set("gurux@gurux.fi")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/gurux/gurux.dlms.android.git")
                        developerConnection.set("scm:git:https://github.com/gurux/gurux.dlms.android.git")
                        url.set("https://github.com/gurux/gurux.dlms.android")
                    }
                }

                val isEnabled = project.hasProperty("sonatypeUsername") && project.hasProperty("sonatypePassword")
                repositories {
                    maven {
                        name = "MavenCentral"
                        url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                        if (isEnabled) {
                            credentials {
                                username = project.property("sonatypeUsername") as String
                                password = project.property("sonatypePassword") as String
                            }
                        }
                    }
                }

                // Signing
                signing {
                    sign(this@create)
                }
            }
        }
    }
}
