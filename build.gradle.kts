plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.springframework.boot") version "3.4.8"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jetbrains.kotlinx.kover") version "0.9.1"
	id("org.sonarqube") version "6.2.0.5505"
}

group = "com.wl2c"
version = "0.0.1-SNAPSHOT"
description = "ELSwhere project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// spring boot
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// mysql
	runtimeOnly("com.mysql:mysql-connector-j")

	// lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// devtools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sonar {
	properties {
		property("sonar.projectKey", "SWM-WeLike2Coding_ELSwhere-backend-monolithic")
		property("sonar.organization", "swm-welike2coding")

		// Kotlin 소스/테스트 경로
		property("sonar.sources", "src/main/kotlin")
		property("sonar.tests", "src/test/kotlin")

		// Kover 리포트 경로
		property("sonar.kotlin.coverage.reportPaths", "build/reports/kover/report.xml")
	}
}