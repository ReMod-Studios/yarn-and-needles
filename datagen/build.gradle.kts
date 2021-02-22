plugins {
    id("com.remodstudios.yarnandneedles.module-conventions")
}

val artificeVersion: String by project

dependencies {
    modApi("com.lettuce.fudge:artifice:$artificeVersion")
    include("com.lettuce.fudge:artifice:$artificeVersion")
}