package mortmyre


import dev.hoot.api.movement.pathfinder.Walker
import dev.hoot.api.movement.Movement
import meteor.plugins.PluginDescriptor
import meteor.ui.overlay.Overlay
import net.runelite.api.Perspective
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.geom.GeneralPath
import java.awt.geom.Path2D
import java.util.*

@PluginDescriptor(
    name = "Mortmyre Bot",
    description = "Example",
    enabledByDefault = false,
    external = true
)
//// Logic.
/// One - Check coords, we start at bank.
/// Two - check inventory, it should be empty.
/// Three - walk to bank
class ExampleOverlay(var plugin: ExamplePlugin) : Overlay() {
//    val wp  =  WorldPoint()
    val mushroomsFinal = Walker.nearestWalkableTile(WorldPoint(3667, 3255, 0))
    val mushroomsFinalTest = WorldPoint(3660, 3227, 0)

    override fun render(graphics: Graphics2D): Dimension? {

        val mushPath = Walker.buildPath(mushroomsFinal, false)

        renderPath(graphics, mushPath)

        return null
    }

    private fun renderPath(graphics: Graphics2D, path: List<WorldPoint>) {
        try {
            val pathToDraw = ArrayList<LocalPoint>(path.size)
            for (element in path) {
                pathToDraw.add(LocalPoint.fromWorld(client, element)!!)
            }
            val generalPath = GeneralPath(Path2D.WIND_EVEN_ODD, pathToDraw.size)
            for (currentTile in pathToDraw) {
                val pathPoint = Perspective.localToCanvas(client, currentTile, client.plane)
                if (pathPoint == null) {
                    graphics.color = Color(255, 0, 255)
                    graphics.draw(generalPath)
                    generalPath.reset()
                } else {
                    if (generalPath.currentPoint == null) {
                        generalPath.moveTo(pathPoint.x.toFloat(), pathPoint.y.toFloat())
                    } else {
                        generalPath.lineTo(pathPoint.x.toFloat(), pathPoint.y.toFloat())
                    }
                }
            }
            if (generalPath.currentPoint != null) {
                graphics.color = Color(255, 0, 255)
                graphics.draw(generalPath)
            }
        } catch (_: Exception) {
        }
    }

    private fun logout() {
        val logoutButton = client.getWidget(182, 8)
        val logoutDoorButton = client.getWidget(69, 23)
        var param1 = -1
        if (logoutButton != null) {
            param1 = logoutButton.id
        } else if (logoutDoorButton != null) {
            param1 = logoutDoorButton.id
        }
        if (param1 == -1) {
            return
        }
    }

    private fun useBank() {
        println("Using bank...")
    }

    private fun depositAll() {
        println("Depositing all...")
    }
fun randInt(r: Random, min: Int, max: Int): Int {
    return r.nextInt(max - min + 1) + min
}



}
