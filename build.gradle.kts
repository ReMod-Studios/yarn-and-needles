plugins {
    id("com.remodstudios.yarnandneedles.fabric-conventions")
}

subprojects.forEach { subProject ->
    dependencies {
        implementation(subProject)
        include(subProject)
    }
}

val mainSourceSet = sourceSets.main.get()

sourceSets {
    register("testmod") {
        compileClasspath += mainSourceSet.compileClasspath
        runtimeClasspath += mainSourceSet.runtimeClasspath
        annotationProcessorPath += mainSourceSet.annotationProcessorPath
    }
}

dependencies {
    // for some reason these dont show up in kotlin dsl... strange - leocth
    configurations["testmodCompile"](mainSourceSet.output)
}

