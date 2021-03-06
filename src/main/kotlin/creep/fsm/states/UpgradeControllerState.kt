package creep.fsm.states

import screeps.api.*
import starter.selectedTarget
import starter.state

class UpgradeControllerState : IState{
    override fun Execute(creep: Creep) : Boolean {


        if(creep.memory.state == "" && creep.store.getFreeCapacity().toFloat() / (creep.store.getCapacity() as Int).toFloat() < 0.5f) {
            creep.memory.state = "MineState"
            MineState().Enter(creep)
        }
        else if (creep.memory.state == ""){
            creep.memory.state = "DeliverState"
            creep.memory.selectedTarget = GetController(creep)
        }

        if(creep.memory.state == "MineState" && MineState().Execute(creep))
            return true;

        if(creep.memory.state == "MineState") {
            MineState().Exit(creep)
            creep.memory.selectedTarget = GetController(creep)
            DelivedState().Enter(creep)
            creep.memory.state = "DeliverState"
        }

        if(creep.memory.state == "DeliverState" && ! DelivedState().Execute(creep)) {
            DelivedState().Exit(creep)
            creep.memory.selectedTarget = ""
            MineState().Enter(creep)
            creep.memory.state = "MineState"
        }

        return true
    }

    private fun GetController(creep: Creep) : String{
       return creep.room.controller?.id as String

    }
    override fun Exit(creep: Creep) {
        creep.say("Ex UpgradeControllerState")
    }

    override fun Enter(creep: Creep) {
        creep.say("En UpgradeControllerState")
    }

    override fun toString(): String {
        return "UpgradeControllerState"
    }

}