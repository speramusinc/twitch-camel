import org.apache.tools.ant.taskdefs.Java
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlinVersion: String by extra { "1.2.41" }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

group = "com.crew.twitch.camel"

val kotlinVersion: String by extra { "1.2.41" }

repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl`
}

apply {
    plugin("kotlin")
    plugin("kotlin-kapt")
    plugin("java-library")
    plugin("java")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"

val springVersion = "5.0.5.RELEASE"
val activemqVersion = "5.15.2"
val camelVersion = "2.21.0"
val jacksonVersion = "2.9.3"

dependencies {
    compile(group = "org.apache.activemq", name =  "activemq-client", version =  activemqVersion)
    compile(group = "org.apache.activemq", name =  "activemq-camel", version =  activemqVersion)

    compile(group = "org.apache.camel", name =  "camel-spring", version =  camelVersion)
    compile(group = "org.apache.camel", name =  "camel-spring-javaconfig", version =  camelVersion)
    compile(group = "org.apache.camel", name =  "camel-aws", version =  camelVersion)
    compile(group = "org.apache.camel", name =  "camel-ognl", version =  camelVersion)
    compile(group = "org.apache.camel", name =  "camel-amqp", version =  camelVersion)
    compile(group = "org.apache.camel", name = "camel-aws", version = camelVersion)

    compile(group = "org.springframework", name = "spring-context", version = springVersion)
    compile(group = "org.springframework", name = "spring-aspects", version = springVersion)
    compile(group = "org.springframework", name = "spring-core", version = springVersion)
    compile(group = "org.springframework", name = "spring-expression", version = springVersion)
    compile(group = "org.springframework", name = "spring-jdbc", version = springVersion)

    compile(group = "com.fasterxml.jackson.core", name = "jackson-core", version = jacksonVersion)
    compile(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    compile(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version = jacksonVersion)
    compile(group = "com.fasterxml.jackson.module", name = "jackson-module-parameter-names", version = jacksonVersion)
    compile(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jdk8", version = jacksonVersion)
    compile(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)

}
