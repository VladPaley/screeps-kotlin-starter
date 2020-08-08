package creep.fsm.states

import screeps.api.*
import starter.selectedTarget
import starter.state

class WithdrawEnergyState : IState {
    override fun Execute(creep: Creep): Boolean {

        if(creep.store.getUsedCapacity() == creep.store.getCapacity())
            return false

        if(PickupEnergyState().Execute(creep))
            return true

        var storage = GetEnergyContainer(creep)

        if (storage != null) {
            if (creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE)
                creep.moveTo(storage)

            if(creep.memory.state == MineState().toString()) {
                MineState().Exit(creep)
                creep.memory.state = toString()
            }
            return true
        }

        if(creep.memory.state != MineState().toString())
            MineState().Enter(creep)

        return MineState().Execute(creep)
    }



    private fun GetEnergyContainer(creep: Creep): StoreOwner? {
        var sources = creep.room.find(FIND_STRUCTURES).filter { x -> x.structureType == STRUCTURE_CONTAINER } as List<StoreOwner>
        sources = sources.filter { x -> x.store.getUsedCapacity(RESOURCE_ENERGY) > 50 }.toMutableList()

        if (sources.size == 0)
            return null

        sources.sortedBy { a ->
            PathFinder.search(a.pos, creep.pos).cost
        }

        return sources[0]
    }

    override fun Exit(creep: Creep) {
        creep.say("Ex WithdrawEnergyState")
        creep.memory.selectedTarget = ""
    }

    override fun Enter(creep: Creep) {
        creep.say("En WithdrawEnergyState")
    }

    override fun toString(): String {
        return "WithdrawEnergyState"
    }
}