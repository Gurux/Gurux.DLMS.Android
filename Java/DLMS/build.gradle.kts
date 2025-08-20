//
// --------------------------------------------------------------------------
//  Gurux Ltd
//
//
//
// Filename:        $HeadURL$
//
// Version:         $Revision$,
//                  $Date$
//                  $Author$
//
// Copyright (c) Gurux Ltd
//
//---------------------------------------------------------------------------
//
//  DESCRIPTION
//
// This file is a part of Gurux Device Framework.
//
// Gurux Device Framework is Open Source software; you can redistribute it
// and/or modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; version 2 of the License.
// Gurux Device Framework is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU General Public License for more details.
//
// More information of Gurux products: http://www.gurux.org
//
// This code is licensed under the GNU General Public License v2.
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jreleaser)
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
                version = project.version.toString()
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
                signing {
                    useGpgCmd()
                    sign(this@create)
                }

                repositories {
                    maven {
                        name = "staging"
                        url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
                    }
                }
            }
        }
    }
}

jreleaser {
    gitRootSearch.set(true)
    project {
        name.set("gurux.dlms.android")
    }

    release {
        github {
            skipRelease.set(false)
            repoOwner.set("Gurux")
            name.set("gurux.dlms.android")
            tagName.set("v{{projectVersion}}")
            releaseName.set("Gurux dlms Android {{projectVersion}}")
            changelog {
                contributors{
                    enabled.set(false)
                }
                preset.set("conventional-commits")
                formatted.set(org.jreleaser.model.Active.ALWAYS)
            }
            token.set(findProperty("githubToken") as String)
        }
    }
    deploy {
        maven {
            mavenCentral {
                register("central") {
                    sign.set(false)
                    active.set(org.jreleaser.model.Active.ALWAYS)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    username.set(findProperty("sonatypeUsername") as String)
                    password.set(findProperty("sonatypePassword") as String)
                    applyMavenCentralRules.set(false)
                    stagingRepositories.add("build/staging-deploy")
                }
            }
        }
    }
}
