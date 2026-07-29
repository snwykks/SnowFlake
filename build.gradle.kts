val projectVersion = project.property("project_version") as String
val projectGroup   = project.property("project_group")   as String
val projectAuthor  = project.property("project_author")  as String

val javaVersion = (project.property("java_version") as String).toInt()

val paperVersion    = project.property("paper_version")    as String
val kamlVersion     = project.property("kaml_version")     as String
val cloudCore       = project.property("cloud_core")       as String
val cloudPaper      = project.property("cloud_paper")      as String
val cloudAnnotation = project.property("cloud_annotations")      as String
val cloudMineEx     = project.property("cloud_minecraft_extras") as String
val serializeCoreVersion = project.property("kotlin_serialize_core_version") as String

plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
    id("com.gradleup.shadow")      version "8.3.0"
}

group   = projectGroup
version = projectVersion

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation("com.charleskorn.kaml:kaml:${kamlVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${serializeCoreVersion}")

    implementation("org.incendo:cloud-core:${cloudCore}")
    implementation("org.incendo:cloud-paper:${cloudPaper}")
    implementation("org.incendo:cloud-annotations:${cloudAnnotation}")
    implementation("org.incendo:cloud-minecraft-extras:${cloudMineEx}")
}

kotlin {
    jvmToolchain(javaVersion)
}

tasks.shadowJar {
    archiveClassifier.set("")

    minimize()

    relocate("org.incendo.cloud", "io.snwykks.snowflake.libs.cloud")
    relocate("com.charleskorn.kaml", "io.snwykks.snowflake.libs.kaml")

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
