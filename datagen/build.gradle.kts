plugins {
    id("com.remodstudios.yarnandneedles.module-conventions")
}

repositories {
	maven("https://maven.dblsaiko.net/")
}

val artificeVersion: String by project

dependencies {
    modApi("de.kb1000:artifice:$artificeVersion")
    include("de.kb1000:artifice:$artificeVersion")
}