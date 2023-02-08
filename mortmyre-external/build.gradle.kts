



version = "1.2.3"

project.extra["PluginName"] = "Mortmyre Bot"
project.extra["PluginDescription"] = "This is an example plugin with a longer description so I can est the text-field constrainsts "

val pluginClass by rootProject.extra { "mortmyre.ExamplePlugin" }


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
