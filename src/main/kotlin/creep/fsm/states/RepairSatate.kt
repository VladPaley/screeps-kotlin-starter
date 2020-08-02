package creep.fsm.states

import screeps.api.*
import screeps.api.structures.Structure
import starter.selectedTarget
import starter.state

class RepairSatate : IState {
    val PERCENT_TO_START_FIXING = 0.5f;

    override fun Execute(creep: Creep): Boolean {

        var target: Structure?


        if (creep.memory.state == "" && creep.store.getFreeCapacity().toFloat() / (creep.store.getCapacity() as Int).toFloat() > 0.5f) {
            creep.memory.state = "WithdrawEnergyState"
            WithdrawEnergyState().Enter(creep)
        } else if (creep.memory.state == "") {
            creep.memory.state = "DeliverState"
        }

        if ((creep.memory.state == "WithdrawEnergyState" || creep.memory.state == "MineState") && (WithdrawEnergyState().Execute(creep)))
            return true;

        if ((creep.memory.state == "WithdrawEnergyState" || creep.memory.state == "MineState")) {
            WithdrawEnergyState().Exit(creep)
            target = GetDamagedStructure(creep)

            if (target != null)
                if (creep.repair(target) == ERR_NOT_IN_RANGE)
                    creep.moveTo(target)

            creep.memory.state = "DeliverState"
        }

        if (creep.memory.state == "DeliverState") {
            if (creep.store.getUsedCapacity() == 0) {
                console.log(creep.memory.state)

                creep.memory.selectedTarget = ""
                WithdrawEnergyState().Enter(creep)
                creep.memory.state = "WithdrawEnergyState"
            } else {
                target = GetDamagedStructure(creep)

                if (target != null)
                    if (creep.repair(target) == ERR_NOT_IN_RANGE)
                        creep.moveTo(target)
            }
        }

        return true
    }

    private fun GetDamagedStructure(creep: Creep): Structure? {
        val targets = creep.room.find(FIND_STRUCTURES).filter { x -> x.structureType != STRUCTURE_CONTROLLER && x.hits.toFloat() / x.hitsMax.toFloat() < PERCENT_TO_START_FIXING }.toTypedArray()

        if (!targets.any())
            return null

        targets.sort { a, b ->
            ((a.hits.toFloat() / a.hitsMax.toFloat()) * 100f).toInt() - ((b.hits.toFloat() / b.hitsMax.toFloat()) * 100f).toInt()
        }

        return targets[0]
    }

    override fun Exit(creep: Creep) {
        creep.say("Ex Repair")
    }

    override fun Enter(creep: Creep) {
        creep.say("En Repair")
    }

    override fun toString(): String {
        return "RepairSatate"
    }
}