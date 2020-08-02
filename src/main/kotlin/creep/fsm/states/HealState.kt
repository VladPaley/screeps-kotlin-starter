package creep.fsm.states

import screeps.api.Creep
import screeps.api.ERR_NOT_IN_RANGE
import screeps.api.FIND_MY_SPAWNS
import starter.selectedTarget
import starter.state

class HealState : IState {
    val DESIRE_TICKS_TO_LIVE = 1300


    override fun Execute(creep: Creep): Boolean {
        if(creep.ticksToLive  > DESIRE_TICKS_TO_LIVE)
            return false
        creep.memory.state = "HealState"


        var spawn = creep.room.find(FIND_MY_SPAWNS)[0]

        if(spawn.renewCreep(creep) == ERR_NOT_IN_RANGE) {
            creep.moveTo(spawn);
        }

        return true;
    }

    override fun Exit(creep: Creep) {
        creep.memory.state = ""
        creep.memory.selectedTarget = ""
        creep.say("ExHeal")
    }

    override fun Enter(creep: Creep) {
        creep.memory.state = "HealState"

        creep.say("EnHeal")
    }
}