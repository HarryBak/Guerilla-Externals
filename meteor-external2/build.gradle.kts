version = "1.0.0"

project.extra["PluginName"] = "ExampleSecond"
project.extra["PluginDescription"] = "This is another example plugin these do nothing"

val pluginClass by rootProject.extra { "example.ExampleSecondPlugin" }



tasks {
    jar {
        manifest {
            attributes(mapOf(
                "Main-Class" to pluginClass,
                "Plugin-Version" to project.version,
                "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                "Plugin-Description" to project.extra["PluginDescription"],
            ))
        }
    }
}
