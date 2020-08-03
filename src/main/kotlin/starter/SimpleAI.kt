package starter


import creep.fsm.FSM
import creep.fsm.states.DelivedState
import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete
import screeps.utils.unsafe.jsObject
import world.room.PopulationManager
import world.room.RoomBuilder
import world.room.RoomNeedsManager

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns.values.firstOrNull() ?: return

    //delete memories of creeps that have passed away
    houseKeeping(Game.creeps)
    val creeps = mainSpawn.room.find(FIND_MY_CREEPS)

    // just an example of how to use room memory
    mainSpawn.room.memory.numberOfCreeps = creeps.count()

    val roomNeedsManager = RoomNeedsManager(mainSpawn.room);
    roomNeedsManager.UpdateNeeds();

    val populationManager = PopulationManager(mainSpawn);
    populationManager.Process(mainSpawn.room);

    val roomBuilder = RoomBuilder()

    roomBuilder.Operate(mainSpawn.room)

    val fsm = FSM()
    console.log("\n")

    creeps.forEach { x ->

        if(x.body.count { y -> y.type == CARRY } > 0) {

            var personalNeed = roomNeedsManager.GetPersonalNeed(x) //personal needs always on top
            var commonNeed = roomNeedsManager.PopTopNeed()
            console.log(" " + commonNeed.getStateToSatisfy()  + " " + commonNeed.score)

            if (personalNeed != null && personalNeed.score >= commonNeed.score) {

                fsm.Operate(x, personalNeed.getStateToSatisfy())
            } else {
                fsm.Operate(x, commonNeed.getStateToSatisfy())
            }
        }

    }
/*
    //make sure we have at least some creeps
    spawnCreeps(Game.creeps.values, mainSpawn)

    // build a few extensions so we can have 550 energy
    val controller = mainSpawn.room.controller
    if (controller != null && controller.level >= 2) {
        when (controller.room.find(FIND_MY_STRUCTURES).count { it.structureType == STRUCTURE_EXTENSION }) {
            0 -> controller.room.createConstructionSite(29, 27, STRUCTURE_EXTENSION)
            1 -> controller.room.createConstructionSite(28, 27, STRUCTURE_EXTENSION)
            2 -> controller.room.createConstructionSite(27, 27, STRUCTURE_EXTENSION)
            3 -> controller.room.createConstructionSite(26, 27, STRUCTURE_EXTENSION)
            4 -> controller.room.createConstructionSite(25, 27, STRUCTURE_EXTENSION)
            5 -> controller.room.createConstructionSite(24, 27, STRUCTURE_EXTENSION)
            6 -> controller.room.createConstructionSite(23, 27, STRUCTURE_EXTENSION)
        }
    }

    //spawn a big creep if we have plenty of energy
    for ((_, room) in Game.rooms) {
        if (room.energyAvailable >= 550) {
            mainSpawn.spawnCreep(
                    arrayOf(
                            WORK,
                            WORK,
                            WORK,
                            WORK,
                            CARRY,
                            MOVE,
                            MOVE
                    ),
                    "HarvesterBig_${Game.time}",
                    options {
                        memory = jsObject<CreepMemory> {
                            this.role = Role.HARVESTER
                        }
                    }
            )
            console.log("hurray!")
        }
    }

    for ((_, creep) in Game.creeps) {
        when (creep.memory.role) {
            Role.HARVESTER -> creep.harvest()
            Role.BUILDER -> creep.build()
            Role.UPGRADER -> creep.upgrade(mainSpawn.room.controller!!)
            else -> creep.pause()
        }
    }
 */

}

private fun spawnCreeps(
        creeps: Array<Creep>,
        spawn: StructureSpawn
) {

    val body = arrayOf<BodyPartConstant>(WORK, CARRY, MOVE)

    if (spawn.room.energyAvailable < body.sumBy { BODYPART_COST[it]!! }) {
        return
    }

    val role: Role = when {
        creeps.count { it.memory.role == Role.HARVESTER } < 2 -> Role.HARVESTER

        creeps.none { it.memory.role == Role.UPGRADER } -> Role.UPGRADER

        spawn.room.find(FIND_MY_CONSTRUCTION_SITES).isNotEmpty() &&
                creeps.count { it.memory.role == Role.BUILDER } < 2 -> Role.BUILDER

        else -> return
    }

    val newName = "${role.name}_${Game.time}"
    val code = spawn.spawnCreep(body, newName, options {
        memory = jsObject<CreepMemory> { this.role = role }
    })

    when (code) {
        OK -> console.log("spawning $newName with body $body")
        ERR_BUSY, ERR_NOT_ENOUGH_ENERGY -> run { } // do nothing
        else -> console.log("unhandled error code $code")
    }
}

private fun houseKeeping(creeps: Record<String, Creep>) {
    if (Game.creeps.isEmpty()) return  // this is needed because Memory.creeps is undefined

    for ((creepName, _) in Memory.creeps) {
        if (creeps[creepName] == null) {
            console.log("deleting obsolete memory entry for creep $creepName")
            delete(Memory.creeps[creepName])
        }
    }
}
