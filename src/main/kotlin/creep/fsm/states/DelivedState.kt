package creep.fsm.states

import screeps.api.*
import screeps.api.structures.Structure
import screeps.api.structures.StructureController
import starter.selectedTarget

class DelivedState : IState {
    override fun Execute(creep: Creep): Boolean {
        if (creep.memory.selectedTarget == "" || creep.store.getUsedCapacity() == 0)
            return false;

        var store = Game.getObjectById<StoreOwner>(creep.memory.selectedTarget)



        if (store != null) {
            var storeAtempt = creep.transfer(store, RESOURCE_ENERGY)

            if (storeAtempt === ERR_NOT_IN_RANGE) {
                creep.moveTo(store.pos)
                return true
            }

            if(storeAtempt != ERR_INVALID_TARGET)
                return storeAtempt === OK
        }

        var consructionSite = Game.getObjectById<ConstructionSite>(creep.memory.selectedTarget)

        if (consructionSite != null) {
            var storeAtempt = creep.build(consructionSite)

            if (storeAtempt === ERR_NOT_IN_RANGE) {
                creep.moveTo(consructionSite.pos)
                return true
            }


            return storeAtempt === OK
        }

        var controller = Game.getObjectById<StructureController>(creep.memory.selectedTarget)

        if (controller != null) {
            var storeAtempt = creep.upgradeController(controller)

            if (storeAtempt === ERR_NOT_IN_RANGE) {
                creep.moveTo(controller.pos)
                return true
            }

            return storeAtempt === OK
        }

        return false;
    }

    override fun Exit(creep: Creep) {
        creep.say("Ex DelivedState")
    }

    override fun Enter(creep: Creep) {
        creep.say("En DelivedState")
    }

    override fun toString(): String {
        return "DelivedState"
    }
}