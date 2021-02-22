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
    annotationProcessor(project(":annotations"))
    testCompileOnly(mainSourceSet.output)
}

