package mortmyre


import dev.hoot.api.items.Bank
import dev.hoot.api.movement.Movement
import dev.hoot.api.movement.pathfinder.Walker
import eventbus.events.GameTick
import meteor.api.Items
import meteor.api.NPCs
import meteor.api.Objects
import meteor.plugins.AIOCrabPlugin.constants.StateController
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import net.runelite.api.*
import net.runelite.api.ObjectID.FUNGI_ON_LOG
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import java.awt.Color
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
class ExamplePlugin : Plugin() {


//    var overlay = overlay(mortmyre.ExampleOverlay(this))

    val objects = Objects
    var config = configuration<ExampleConfig>()
    val timeout = 1000
    var state: String = "Startup"
    val mushroomsFinal = Walker.nearestWalkableTile(WorldPoint(3667, 3255, 0))
    val mushroomsArea = WorldArea(3667, 3255, 0, 2, 0)
    private val bankArea = WorldArea(4652, 3208, 2, 6, 0)

    val bank = WorldPoint(3652, 3208, 0)
    private var ticktimer = 5
    private val rand = Random()
    override fun onGameTick(it: GameTick) {
        if (ticktimer > 0) {
            ticktimer--
            return
        }
        val location = client.localPlayer!!.worldLocation

        if (state == "Startup" && client.localPlayer?.isIdle == true &&
            !mushroomsArea.contains(location)
        ) {
            Movement.walkTo(mushroomsFinal)
        } else if (state == "Startup"
            && client.localPlayer?.isIdle == true
            && mushroomsArea.contains(location)
        )
            println("We made it here!")
        state = "Bloom"
//        if (Items.equipmentContains("Enchanted emerald sickle (b)")) {
        if (Items.inventoryContains("Enchanted emerald sickle (b)")) {
            println("Found sickle!")
            if (Items.isFull()) {
//                drakansTeleport()
                bankFungus()
            }
            if (!Items.isFull()) {
                Objects.getAll(3509)?.forEach {
                    println(it.name)
                    it.interact("Pick")
                }
                println("Inventory not full")

                if (!client.localPlayer?.isAnimating!!) {
                    println("finding fungi....")
//                    Items.getFirst(ItemID.ENCHANTED_EMERALD_SICKLE_B, InventoryID.EQUIPMENT)?.interact(1)
                    if (Objects.getAll(3509).isNullOrEmpty()) {
                        Items.getFirst(ItemID.ENCHANTED_EMERALD_SICKLE_B, InventoryID.INVENTORY).let {
                            print(it?.name)
                            it?.interact("Cast Bloom")
                        }
                    }
                    println(ticktimer)


                }

            }
        }
    }


    private fun checkPrayer() {
        if (client.getBoostedSkillLevel(Skill.PRAYER) > 20) {
            println("Low prayer points")
            Items.getFirst(ItemID.ARDOUGNE_CLOAK_2).let {
                it?.interact("Kandarin Monastery")
            }
            Objects.getFirst(409).let {
                it?.interact("Pray-at")
            }
        }
    }

    private fun drakansTeleport() {
        println("Trying to teleport...")
        Items.getFirst(ItemID.DRAKANS_MEDALLION, InventoryID.INVENTORY)?.interact("Ver Sinhaza")
        ticktimer = 10
    }

    private fun openBank() {
        val banker = NPCs.getFirst("Banker")
        if (Movement.isWalking()) {
            ticktimer = 1
            return
        }
        if (!bankArea.contains(client.localPlayer)) {
            Movement.walkTo(bankArea)
            ticktimer = 2
        } else {
            banker?.interact("Bank")
            ticktimer = 2
        }
    }

    private fun bankFungus() {
        if (!Bank.isOpen()) {
            if (!Movement.isRunEnabled()) {
                Movement.toggleRun()
            }
            openBank()
        }


        if (Items.inventoryContains(ItemID.MORT_MYRE_FUNGUS)) {
            val kebab = Items.getFirst(ItemID.MORT_MYRE_FUNGUS)
            Items.deposit(kebab!!, 9999)
            ticktimer = 2
            return
        }
        ticktimer = 2
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

    override fun onStart() {
        println("Started Playground")
    }

    override fun onStop() {
        println("Stopped playground")
    }
}

