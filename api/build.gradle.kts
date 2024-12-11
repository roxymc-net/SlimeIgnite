plugins {
    id("java-library")
    id("maven-publish")
}

val minecraftVersion = project.property("minecraft-version")

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")

    api("net.roxymc.slimeloader:slime-loader:1.0-SNAPSHOT") {
        isTransitive = false
    }

    // remember to always keep these up-to-date with slime-loader
    api("net.kyori:adventure-nbt:4.17.0") {
        isTransitive = false
    }
    api("com.github.luben:zstd-jni:1.5.6-6")
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        val repoType = if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"
        maven("https://repo.roxymc.net/${repoType}") {
            name = "roxymc"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifactId = "${rootProject.name}-${project.name}".lowercase()

            from(components["java"])
        }
    }
}
