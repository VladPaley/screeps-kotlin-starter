package creep.fsm.states

import screeps.api.*
import screeps.api.structures.Structure
import screeps.api.structures.StructureContainer
import screeps.api.structures.StructureExtension
import screeps.api.structures.StructureSpawn
import starter.selectedTarget
import starter.state

open class CollectState : IState {
    override fun Execute(creep: Creep): Boolean {

        try {

        }
        catch (exceprion : Exception) {
            console.log(exceprion)
        }


        if(creep.memory.state != "MineState" && creep.memory.state != "DeliverState")
            creep.memory.state = ""

    if (creep.memory.state == "" && creep.store.getFreeCapacity().toFloat() / (creep.store.getCapacity() as Int).toFloat() < 0.5f) {
        creep.memory.state = "MineState"
        MineState().Enter(creep)
    } else if (creep.memory.state == "") {
        creep.memory.state = "DeliverState"
        creep.memory.selectedTarget = GetStorage(creep)
    }

        if (creep.memory.state == "MineState" && MineState().Execute(creep))
            return true;

        if (creep.memory.state == "MineState") {
            MineState().Exit(creep)
            creep.memory.selectedTarget = GetStorage(creep)
            DelivedState().Enter(creep)
            creep.memory.state = "DeliverState"
        }

        if (creep.memory.state == "DeliverState" && !DelivedState().Execute(creep)) {
            DelivedState().Exit(creep)
            creep.memory.selectedTarget = ""
            MineState().Enter(creep)
            creep.memory.state = "MineState"
        }

        return true
    }

    private fun GetStorage(creep: Creep): String {
        try {
            var spawns = creep.room.find(FIND_STRUCTURES).filter { x -> x.structureType == STRUCTURE_SPAWN }.toTypedArray() as Array<StoreOwner>
            spawns = spawns.filter { x -> x.store.getFreeCapacity(RESOURCE_ENERGY) > 0 }.toTypedArray()
            var extentions = creep.room.find(FIND_STRUCTURES).filter { x -> x.structureType == STRUCTURE_EXTENSION }.toTypedArray() as Array<StoreOwner>
            extentions = extentions.filter { x -> x.store.getFreeCapacity(RESOURCE_ENERGY) > 0 }.toTypedArray()
            var container = creep.room.find(FIND_STRUCTURES).filter { x -> x.structureType == STRUCTURE_CONTAINER }.toTypedArray() as Array<StoreOwner>
            container = container.filter { x -> x.store.getFreeCapacity(RESOURCE_ENERGY) > 0 }.toTypedArray()
            var towers = creep.room.find(FIND_STRUCTURES).filter { x -> x.structureType == STRUCTURE_TOWER }.toTypedArray() as Array<StoreOwner>
            towers = towers.filter { x -> x.store.getFreeCapacity(RESOURCE_ENERGY) > 0 }.toTypedArray()

            if (spawns.size > 0)
                return GetClosestAwalibleStorate(spawns, creep);
            if (extentions.size > 0)
                return GetClosestAwalibleStorate(extentions, creep);
            if (towers.size > 0)
                return GetClosestAwalibleStorate(towers, creep);
            if (container.size > 0)
                return GetClosestAwalibleStorate(container, creep);
        }
        catch (exception : Exception) {
            console.log(exception)
        }


        return ""
    }

    private fun GetClosestAwalibleStorate(structures: Array<StoreOwner>, creep: Creep): String {
        structures.filter { x -> x.store.getFreeCapacity() != 0 }
        if (structures.size > 0) {
            structures.sort { a, b ->
                a.pos.getRangeTo(creep.pos) - b.pos.getRangeTo(creep);
            }
            return structures[0].id
        }
        return ""
    }

    override fun Exit(creep: Creep) {
        creep.say("Ex CollectState")

    }

    override fun Enter(creep: Creep) {
        if(creep.memory.state != "MineState" && creep.memory.state != "DeliverState")
            creep.memory.state = ""

        creep.say("En CollectState")
    }

    override fun toString(): String {
        return "CollectState"
    }

}