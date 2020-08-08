package creep.fsm.states

import screeps.api.Creep
import screeps.api.ERR_NOT_IN_RANGE
import screeps.api.FIND_MY_SPAWNS
import screeps.api.structures.StructureSpawn

class RecycleState : IState {
    override fun Execute(creep: Creep): Boolean {
        val spawn = creep.room.find(FIND_MY_SPAWNS)[0] as StructureSpawn

        if(spawn.recycleCreep(creep) == ERR_NOT_IN_RANGE)
            creep.moveTo(spawn)

        return true
    }

    override fun Exit(creep: Creep) {
        creep.say("I'm useful!!")

    }

    override fun Enter(creep: Creep) {
        creep.say("Goodbye cruel world...")
    }

    override fun toString(): String {
        return "RecycleState"
    }
}