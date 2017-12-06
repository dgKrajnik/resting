import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "1.0-SNAPSHOT"
group = "com.dgkrajnik"

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.0"

    repositories {
        mavenCentral()
    }
    
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
        classpath(kotlinModule("allopen", kotlin_version))
        classpath("org.springframework.boot:spring-boot-gradle-plugin:1.5.9.RELEASE")
        classpath("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.0")
    }
    
}

apply {
    plugin("java")
    plugin("kotlin")
    plugin("kotlin-spring")
    plugin("org.springframework.boot")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jdk8", kotlin_version))
    testCompile("junit", "junit", "4.12")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin")
    compile("javax.inject:javax.inject:1")
    compile("org.springframework.boot:spring-boot-starter-security")
    testCompile("org.mockito:mockito-core:1.+")
    testCompile("org.springframework.boot:spring-boot-starter-test")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

