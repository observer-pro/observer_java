fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java") // Java support
    id("org.jetbrains.intellij") version "1.13.3"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

tasks.register("createJavaClass", JavaCompile::class) {

    val file = File("src/main/java/pro/sky/observer_java/constants/Properties.java")
    file.writeText(String.format("""
        package pro.sky.observer_java.constants;

        public class Properties {
          public static String VERSION = "%s";
        }
    """.trimIndent(),properties("pluginVersion").get()))
}

tasks.register("createReadMe", JavaCompile::class) {

    val file = File("README.md")
    file.writeText(String.format("""
# IDEA IDE Plugin. 

To install the plugin: 
1. Download Observer App-%s.zip from build\distributions folder.
2. Press Ctrl+Alt+S to open the IDE settings and select Plugins.
3. On the Plugins page, click The Settings button (looks like a gear) and then click Install Plugin from Disk….
4. Select the plugin archive file and click OK.
5. Click OK to apply the changes and restart the IDE if prompted.
    """.trimIndent(),properties("pluginVersion").get()))
}


// Configure project's dependencies
repositories {
    mavenCentral()
}



// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    implementation ("io.socket:socket.io-client:2.1.0"){
         // exclude("org.json", "json")
    }
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.commonmark:commonmark:0.21.0")
}


// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName = properties("pluginName").get()
    version = properties("platformVersion").get()
    type = properties("platformType").get()
    updateSinceUntilBuild.set(false)
}


tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("241.*")
    }
}
