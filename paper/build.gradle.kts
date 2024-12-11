import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    id("com.gradleup.shadow") version "8.3.5"
    id("io.github.gliczdev.access-widen") version "1.0.0"
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
val minecraftVersion = project.property("minecraft-version")

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly("space.vectrix.ignite:ignite-api:1.1.0")
    compileOnly("org.spongepowered:mixin:0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
    implementation(project(":api"))
    accessWiden("io.papermc.paper:paper-server:userdev-$minecraftVersion-R0.1-SNAPSHOT")
}

accessWideners {
    files.from(fileTree(sourceSets.main.get().resources.srcDirs.first()) {
        include("*.accesswidener")
    })
}

tasks {
    compileJava {
        dependsOn(applyAccessWideners)
    }

    withType<Jar> {
        archiveBaseName = "${rootProject.name}-${project.name}-$minecraftVersion"
        archiveClassifier = null
    }
}
