import net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder

plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version ("0.6.0")
}

version = "1.2.1"
description = "Provides Redis clients and implements the helper Messaging system using Jedis."
project.ext.set("name", "helper-redis")

dependencies {
    val jedisVersion = "4.4.6"
    implementation("redis.clients", "jedis", jedisVersion) {
        exclude("org.slf4j", "slf4j-api")
    }
    compileOnly(project(":helper"))
}

tasks {
    shadowJar {
        val shadePattern = "me.lucko.helper.redis.external."
        relocate("redis.clients.jedis", shadePattern + "jedis")
        relocate("org.apache.commons.pool", shadePattern + "pool")
    }
}

paper {
    main = "me.lucko.helper.redis.plugin.HelperRedisPlugin"
    name = project.ext.get("name") as String
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    author = "Luck"
    serverDependencies {
        register("helper") {
            required = true
            load = RelativeLoadOrder.BEFORE
        }
    }
}