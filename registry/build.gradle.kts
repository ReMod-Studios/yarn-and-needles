plugins {
    id("com.remodstudios.yarnandneedles.module-conventions")
}

val activejCodegenVersion: String by project

dependencies {
    api("io.activej:activej-codegen:$activejCodegenVersion")
}