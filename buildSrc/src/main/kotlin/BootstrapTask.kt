import com.savvasdalkitsis.jsonmerger.JsonMerger
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.file.Paths
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
open class BootstrapTask : DefaultTask() {

    private fun formatDate(date: Date?) = with(date ?: Date()) {
        SimpleDateFormat("yyyy-MM-dd").format(this)
    }

    private fun hash(file: ByteArray): String {
        return MessageDigest.getInstance("SHA-512").digest(file).fold("", { str, it -> str + "%02x".format(it) }).toUpperCase()
    }
    fun clearAndBuild() {
        try {
            val file = File("plugins.json")
            if (file.exists()) {
                file.writeText("")
            } else {
                file.createNewFile()
            }

            val pluginsReleaseDir = File("plugin-release")
            if (!pluginsReleaseDir.exists()) {
                pluginsReleaseDir.mkdir()
            } else {
                project.rootProject.delete(project.rootProject.fileTree(mapOf(
                    "dir" to "plugin-release",
                    "include" to "**/*.jar",
                    "exclude" to "**/*-sources.jar"
                )))
            }


        } catch (e: Exception) {
            println("An error occurred while clearing and building: ${e.message}")
        }
    }
    private fun getBootstrap(filename: String): JSONArray? {
        val bootstrapFile = File(filename).readLines()

        return JSONObject("{\"plugins\":$bootstrapFile}").getJSONArray("plugins")
    }

    @TaskAction
    fun boostrap() {



            clearAndBuild()

        if (project == project.rootProject) {
            val bootstrapDir = File("${project.projectDir}")
            val bootstrapReleaseDir = File("${project.projectDir}/plugin-release")

            bootstrapReleaseDir.mkdirs()

            val plugins = ArrayList<JSONObject>()
            val baseBootstrap = getBootstrap("$bootstrapDir/plugins.json") ?: throw RuntimeException("Base bootstrap is null!")

            project.subprojects.forEach {
                if (it.project.properties.containsKey("PluginName") && it.project.properties.containsKey("PluginDescription")) {
                    var pluginAdded = false
                    val plugin = it.project.tasks["jar"].outputs.files.singleFile

                    val releases = ArrayList<JsonBuilder>()

                    releases.add(JsonBuilder(
                            "version" to it.project.version,
                            "date" to formatDate(Date()),
                            "url" to "${project.rootProject.extra.get("GithubUrl")}/raw/main/plugin-release/${it.project.name}-${it.project.version}.jar",
                            "sha512sum" to hash(plugin.readBytes())
                    ))

                    val pluginObject = JsonBuilder(
                            "name" to it.project.extra.get("PluginName"),
                            "id" to nameToId(it.project.extra.get("PluginName") as String),
                            "description" to it.project.extra.get("PluginDescription"),
                            "projectUrl" to it.project.extra.get("ProjectSupportUrl"),
                            "releases" to releases.toTypedArray()
                    ).jsonObject()

                    for (i in 0 until baseBootstrap.length()) {
                        val item = baseBootstrap.getJSONObject(i)

                        if (item.get("id") != nameToId(it.project.extra.get("PluginName") as String)) {
                            continue
                        }

                        if (it.project.version.toString() in item.getJSONArray("releases").toString()) {
                            pluginAdded = true
                            plugins.add(item)
                            break
                        }

                        plugins.add(JsonMerger(arrayMergeMode = JsonMerger.ArrayMergeMode.MERGE_ARRAY).merge(item, pluginObject))
                        pluginAdded = true
                    }

                    if (!pluginAdded)
                    {
                        plugins.add(pluginObject)
                    }

                    plugin.copyTo(Paths.get(bootstrapReleaseDir.toString(), "${it.project.name}-${it.project.version}.jar").toFile())
                }
            }

            File(bootstrapDir, "plugins.json").printWriter().use { out ->
                out.println(plugins.toString())
            }
        }

    }
}
