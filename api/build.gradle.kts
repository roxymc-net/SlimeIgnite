plugins {
    id("java-library")
    id("maven-publish")
}

val minecraftVersion = project.property("minecraft-version")

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    api("net.roxymc.slimeloader:slime-loader:1.0-SNAPSHOT")
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
