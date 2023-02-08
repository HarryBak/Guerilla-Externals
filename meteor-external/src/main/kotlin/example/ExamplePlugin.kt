package example


import dev.hoot.api.commons.Time
import dev.hoot.api.items.Bank
import dev.hoot.api.magic.Magic
import dev.hoot.api.magic.Regular
import dev.hoot.api.movement.Movement
import dev.hoot.api.util.Randomizer
import eventbus.events.ClientTick
import eventbus.events.GameTick
import eventbus.events.MenuOptionClicked
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import meteor.api.Items
import meteor.api.Loots
import meteor.api.NPCs
import meteor.api.Objects
import meteor.plugins.AIOCrabPlugin.constants.StateController
import net.runelite.api.*
import net.runelite.api.queries.GameObjectQuery
import net.runelite.api.queries.NPCQuery
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.api.coords.WorldPoint

import java.util.*
import java.util.function.Predicate

@PluginDescriptor(
    name = "Playground",
    description = "Example",
    enabledByDefault = false,
    external = true
)
//// Logic.
/// One - Check coords, we start at bank.
/// Two - check inventory, it should be empty.
/// Three - walk to bank
class ExamplePlugin : Plugin() {
    val objects = Objects
    var config = configuration<ExampleConfig>()
    val timeout = 1000
    val state = StateController.ZERO
    val mushrooms = WorldPoint(3667, 3225, 0)
    val bank = WorldPoint(3652, 3208, 0)
    private var ticktimer = 0
    private val rand = Random()
    override fun onGameTick(it: GameTick) {
        if (ticktimer > 0) {
            ticktimer--
            println(ticktimer)
            return
        }
        if (Bank.Inventory.getFirst("Law rune") == null) {
            println("Out of runes, logging out.")
            logout()
        }

        if (client.localPlayer?.isIdle == true && Loots.exists("Wine of zamorak") && Bank.Inventory.getFirst("Law rune") != null) {
            val wine = Loots.getFirst(ItemID.WINE_OF_ZAMORAK)?.let {
                println("Can see Wine")
                println(it.tile)
                println(it.clickPoint)
                Magic.cast(Regular.TELEKINETIC_GRAB, it)
                ticktimer = randInt(rand, config.MinimumDelay(), config.MaximumDelay())

            }
        }
//        if (client.localPlayer?.isIdle == true && !Bank.isOpen() && Items.getAll() != null) {
//            useBank()
//        }
//        else if (Bank.isOpen()) {
//            depositAll()
//            if (Items.getAll() == null) {
//                Movement.walkTo(mushrooms)
//            }
//            else {
//                println("Items not null, not closing")
//            }
//        }
    }

//    private fun useBank() {
//        if (!Bank.bankPinIsOpen() && !Bank.isOpen()) {
//            println("Going to bank all.")
//            objects.getFirst("Bank booth")?.interact("Bank")
//        }
//        else {
//            depositAll()
//        }
//    }
//
//    private fun depositAll() {
//        println(Items.getAll())
//        println(Items.getAll() != null)
//        if (Items.getAll() != null) {
//            Bank.depositInventory()
//            StateController.TWO
//
//        }
//    }

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
fun randInt(r: Random, min: Int, max: Int): Int {
    return r.nextInt(max - min + 1) + min
}

override fun onStart() {
    println("Started Playground")
}

override fun onStop() {
    println("Stopped playground")
}


}
