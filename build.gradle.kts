plugins {
  java
}

repositories {
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
  implementation("org.zeroturnaround:zt-zip:1.14")
  implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}