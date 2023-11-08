plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "pro.sky"
version = "1.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.1.4")
//    type.set("IC") // Target IDE Platform

//    plugins.set(listOf(/* Plugin Dependencies */))
}


dependencies{
    implementation ("io.socket:socket.io-client:2.1.0"){
      //  exclude("org.json", "json")
    }
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.0")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("org.lucee:txtmark:0.16.0")

}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }


    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
    }
}
