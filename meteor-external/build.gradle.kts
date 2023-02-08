



version = "1.0.0"

project.extra["PluginName"] = "Playground"
project.extra["PluginDescription"] = "This is an example plugin with a longer description so I can est the text-field constrainsts "

val pluginClass by rootProject.extra { "example.ExamplePlugin" }


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
