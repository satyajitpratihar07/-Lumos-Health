pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven {
      url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
    maven {
      url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
  }
}

rootProject.name = "Face Health Monitor"

include(":app")
