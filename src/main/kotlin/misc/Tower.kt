package misc

import screeps.api.FIND_HOSTILE_CREEPS
import screeps.api.FIND_MY_STRUCTURES
import screeps.api.FIND_STRUCTURES
import screeps.api.FilterOption
import screeps.api.structures.StructureTower

class TowerOperator {

    fun Operate(tower: StructureTower) {
        var closestHostile = tower.pos.findClosestByRange(FIND_HOSTILE_CREEPS)

        if (closestHostile != null) {
            tower.attack(closestHostile)
            return
        }

        var closestDamagedStructure = tower.room.find(FIND_STRUCTURES).filter { x -> x.hits.toFloat() / x.hitsMax.toFloat() < 0.5f }.sortedBy { a -> tower.pos.getRangeTo(a.pos) }

        if(closestDamagedStructure.size > 0)
            tower.repair(closestDamagedStructure[0])

    }
}