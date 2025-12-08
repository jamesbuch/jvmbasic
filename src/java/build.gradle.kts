/*
 * JVM BASIC Compiler - Gradle Build Configuration
 *
 * This builds the new ANTLR4-based Java compiler alongside the existing C++ compiler.
 *
 * Build commands:
 *   ./gradlew build          - Compile everything
 *   ./gradlew generateGrammarSource - Generate ANTLR parser/lexer/visitor/listener
 *   ./gradlew test           - Run tests
 *   ./gradlew jar            - Create executable JAR
 *   ./gradlew clean          - Clean build artifacts
 */

plugins {
    java
    antlr
    application
}

group = "com.jvmbasic"
version = "2.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // ANTLR4 for parser generation
    antlr("org.antlr:antlr4:4.13.2")
    implementation("org.antlr:antlr4-runtime:4.13.2")

    // ASM for bytecode generation (recommended)
    implementation("org.ow2.asm:asm:9.9")
    implementation("org.ow2.asm:asm-util:9.9")
    implementation("org.ow2.asm:asm-commons:9.9")

    // Apache BCEL as alternative bytecode library
    implementation("org.apache.bcel:bcel:6.11.0")

    // BouncyCastle for advanced cryptography
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.77")

    // Jetty for embedded web server
    implementation("org.eclipse.jetty:jetty-server:11.0.19")
    implementation("org.eclipse.jetty:jetty-servlet:11.0.19")
    implementation("org.eclipse.jetty:jetty-util:11.0.19")
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")

    // Redis client (Jedis)
    implementation("redis.clients:jedis:5.1.0")

    // Memcached client (Spymemcached)
    implementation("net.spy:spymemcached:2.12.3")

    // Testing - JUnit 6.0.1
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:2.0.1")
}

// Configure ANTLR generation
tasks.generateGrammarSource {
    // Generate both listener AND visitor
    arguments = listOf("-visitor", "-listener", "-package", "com.jvmbasic.grammar")
    outputDirectory = layout.buildDirectory.dir("generated-src/antlr/main/com/jvmbasic/grammar").get().asFile
}

// Source sets configuration
sourceSets {
    main {
        java {
            srcDirs(
                "com/jvmbasic",
                layout.buildDirectory.dir("generated-src/antlr/main")
            )
        }
        antlr {
            setSrcDirs(listOf(file("com/jvmbasic/grammar")))
        }
    }
    test {
        java {
            srcDirs("com/jvmbasic/test")
        }
    }
}

// Ensure ANTLR generation happens before compilation
tasks.compileJava {
    dependsOn(tasks.generateGrammarSource)
}

// Application configuration
application {
    mainClass.set("com.jvmbasic.Main")
}

// JAR configuration
tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.jvmbasic.Main",
            "Implementation-Title" to "JVM BASIC Compiler",
            "Implementation-Version" to project.version
        )
    }

    // Create fat JAR with all dependencies
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        // Exclude signature files from signed JARs (like BouncyCastle)
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }
}

// Test configuration
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Custom task to show grammar info
tasks.register("grammarInfo") {
    group = "help"
    description = "Show ANTLR grammar generation info"
    doLast {
        println("""
            |ANTLR Grammar Generation:
            |  Lexer:    com/jvmbasic/grammar/JvmBasicLexer.g4
            |  Parser:   com/jvmbasic/grammar/JvmBasicParser.g4
            |  Output:   build/generated-src/antlr/main/com/jvmbasic/grammar/
            |
            |Generated files:
            |  - JvmBasicLexer.java
            |  - JvmBasicParser.java
            |  - JvmBasicParserBaseListener.java
            |  - JvmBasicParserListener.java
            |  - JvmBasicParserBaseVisitor.java
            |  - JvmBasicParserVisitor.java
            |
            |Build commands:
            |  ./gradlew generateGrammarSource  - Generate parser only
            |  ./gradlew build                  - Full build
            |  ./gradlew jar                    - Create executable JAR
        """.trimMargin())
    }
}
