package creep.fsm.states

import screeps.api.*
import starter.state

class PickupEnergyState : IState {
    override fun Execute(creep: Creep): Boolean {
        var target = GetFreeEnergy(creep)

        if (target != null) {
            console.log(creep.pickup(target))
            if (creep.pickup(target) == ERR_NOT_IN_RANGE)
                creep.moveTo(target)
            else
                return false

            return true
        }
        return false
    }

    private fun GetFreeEnergy(creep: Creep): Resource? {
        var energies = creep.room.find(FIND_DROPPED_RESOURCES).filter { x -> x.resourceType == RESOURCE_ENERGY }

        if (energies.size == 0)
            return null

        energies.sortedBy { a -> PathFinder.search(a.pos, creep.pos).cost }
        console.log(energies)
        return energies[0]

    }

    override fun Exit(creep: Creep) {
        creep.say("En PickupEnergyState")

    }

    override fun Enter(creep: Creep) {
        creep.say("Ex PickupEnergyState")
    }

}