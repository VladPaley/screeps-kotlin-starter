package creep.fsm.states

import screeps.api.*
import starter.selectedTarget

class MineState : IState {

    val CREEP_DISTANCE_FACTOR = 10;
    val ENERGY_LACK_FACTOR = 200;

    override fun Execute(creep: Creep): Boolean {
        if (creep.memory.selectedTarget == "")
            creep.memory.selectedTarget = GetSource(creep);

        val source = Game.getObjectById<Source>(creep.memory.selectedTarget)

        if (source == null) {
            console.log(creep.memory.selectedTarget)
            return false
        }

        if (creep.harvest(source) == ERR_NOT_IN_RANGE) {
            creep.moveTo(source);
        } else if (creep.harvest(source) == ERR_INVALID_TARGET || creep.harvest(source) == ERR_NOT_ENOUGH_RESOURCES)
            creep.memory.selectedTarget = GetSource(creep);


        return creep.store.getUsedCapacity() != creep.store.getCapacity()
    }

    private fun GetSource(creep: Creep): String {
        val sources = creep.room.find(FIND_SOURCES)
        val crees = creep.room.find(FIND_MY_CREEPS);
        sources.sort { a, b ->
            var creepsAFactor = 0
            var creepsBFactor = 0

            crees.forEach { c ->
                if (c.memory.selectedTarget == a.id)
                    creepsAFactor++;

                if (c.memory.selectedTarget == b.id)
                    creepsBFactor++;
            }
            var aEmptinessFactor = ((a.energyCapacity.toFloat() - a.energy.toFloat()) / ENERGY_LACK_FACTOR).toInt()
            var bEmptinessFactor = ((b.energyCapacity.toFloat() - b.energy.toFloat()) / ENERGY_LACK_FACTOR).toInt()

            console.log( "  " + aEmptinessFactor + "  " + a.id)
            console.log( "  " + bEmptinessFactor + "  " + b.id)
            console.log( " a cost:  " + (PathFinder.search(a.pos, creep.pos).cost + creepsAFactor * CREEP_DISTANCE_FACTOR + aEmptinessFactor) + "  " + b.id)
            console.log( "b const   " + ((PathFinder.search(b.pos, creep.pos).cost + creepsBFactor * CREEP_DISTANCE_FACTOR) + bEmptinessFactor) + "  " + b.id)


            (PathFinder.search(a.pos, creep.pos).cost + creepsAFactor * CREEP_DISTANCE_FACTOR + aEmptinessFactor) -
                    ((PathFinder.search(b.pos, creep.pos).cost + creepsBFactor * CREEP_DISTANCE_FACTOR) + bEmptinessFactor)
        }

        console.log(sources[0].id)
        return sources[0].id
    }

    //   private fun GetAvaliblePositionsOnSurce(source: Source) : Int {

    //  }

    override fun Exit(creep: Creep) {
        creep.memory.selectedTarget = ""
        creep.say("Ex MineState")
    }

    override fun Enter(creep: Creep) {
        creep.say("En MineState")
    }

    override fun toString(): String {
        return "MineState"
    }
}