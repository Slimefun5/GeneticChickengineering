plugins {
    java
    id("com.gradleup.shadow") version "9.3.2"
    id("io.github.intisy.github-gradle") version "1.8.3"
}

group = "net.guizhanss"
description = "An addon for Slimefun that adds resource chickens, rewritten by ybw0014."

apply(from = "https://raw.githubusercontent.com/Slimefun5/gradle/stable/slimefun-addon.gradle")

repositories {
    // WildStackerAPI
    maven("https://repo.bg-software.com/repository/api/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    // Shaded + relocated into our own jar, same as the original pom's shade config.
    // NOTE: GuizhanLib-api is intentionally NOT a dependency here (see GeneticChickengineering's
    // class javadoc) - its jar is class-file version 60 (Java 16), which a Java-8 javac cannot read
    // as a compile dependency at all, and (separately) its AbstractAddon/AddonConfig/SlimefunLocalization
    // classes hard-bind to Slimefun's pre-fork API package. The handful of
    // Slimefun-independent pieces this addon used (Scheduler, the command framework, chat/version/
    // localization utils) are vendored as plain Java-8 source under
    // net.guizhanss.gcereborn.libs.guizhanlib instead.
    implementation("org.bstats:bstats-bukkit:3.1.0")

    // Soft-depends: present on the classpath only, never shaded.
    // StackMob is NOT a dependency here - its jars are Java 11+ class files, unreadable by a Java-8
    // javac; IntegrationService talks to it purely via reflection instead (see its class javadoc).
    compileOnly("com.bgsoftware:WildStackerAPI:2024.4")
}

tasks {
    shadowJar {
        relocate("org.bstats", "net.guizhanss.gcereborn.libs.bstats")
        minimize()
        exclude("io/github/thebusybiscuit/slimefun5/**")
    }
}
