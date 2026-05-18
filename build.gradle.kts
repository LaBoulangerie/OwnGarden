plugins {
  java
  id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
  implementation("org.zeroturnaround:zt-zip:1.14")
  implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
  runServer {
    minecraftVersion("1.21.11")
  }
}