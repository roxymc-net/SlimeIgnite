plugins {
    id("java")
}

subprojects {
    plugins.apply("java")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.roxymc.net/releases")
        maven("https://repo.roxymc.net/snapshots")
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release = 21
            dependsOn(clean)
        }
    }
}
