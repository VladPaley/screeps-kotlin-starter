package creep.fsm.states

import screeps.api.*
import starter.selectedTarget
import starter.state

class BuildState : IState {
    override fun Execute(creep: Creep) : Boolean {

        if(creep.memory.state == "" && creep.store.getFreeCapacity().toFloat() / (creep.store.getCapacity() as Int).toFloat() > 0.5f) {
            creep.memory.state = "WithdrawEnergyState"
            WithdrawEnergyState().Enter(creep)
        }
        else if (creep.memory.state == ""){
            creep.memory.state = "DeliverState"
            creep.memory.selectedTarget = GetConstructionSite(creep)
        }

        if((creep.memory.state == "WithdrawEnergyState" || creep.memory.state == "MineState") && (WithdrawEnergyState().Execute(creep)))
            return true;

        if((creep.memory.state == "WithdrawEnergyState" || creep.memory.state == "MineState")) {
            WithdrawEnergyState().Exit(creep)
            creep.memory.selectedTarget = GetConstructionSite(creep)
            DelivedState().Enter(creep)
            creep.memory.state = "DeliverState"
        }

        if(creep.memory.state == "DeliverState" && ! DelivedState().Execute(creep)) {
            DelivedState().Exit(creep)
            creep.memory.selectedTarget = ""
            WithdrawEnergyState().Enter(creep)
            creep.memory.state = "WithdrawEnergyState"
        }

        return true
    }

    private fun GetConstructionSite(creep: Creep) : String{
        var target = GetClosestContructionSiteOfType(STRUCTURE_EXTENSION, creep);

        if(target != null)
            return target.id;

        target = GetClosestContructionSiteOfType(STRUCTURE_TOWER, creep);

        if(target != null)
            return target.id;

        target = GetClosestContructionSiteOfType(STRUCTURE_ROAD, creep);

        if(target != null)
            return target.id;

        return ""
    }

    private fun GetClosestContructionSiteOfType(type : StructureConstant, creep: Creep) : ConstructionSite? {
        var contstructionStates = creep.room.find(FIND_CONSTRUCTION_SITES)

        var sites = contstructionStates.filter { x-> x.structureType == type }

        if(sites.any()) {
            contstructionStates.sort { a, b ->
                b.pos.getRangeTo(creep.pos) - a.pos.getRangeTo(creep)
            }
            return sites[0]
        }
        return null
    }

    override fun Exit(creep: Creep) {
        creep.say("Ex Build")

    }

    override fun Enter(creep: Creep) {
        creep.say("En Build")

    }

    override fun toString(): String {
        return "BuildState"
    }
}