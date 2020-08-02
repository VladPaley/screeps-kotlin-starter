package misc

import screeps.api.FIND_HOSTILE_CREEPS
import screeps.api.FIND_MY_STRUCTURES
import screeps.api.FilterOption
import screeps.api.structures.StructureTower

class TowerOperator {

    fun Operate(tower: StructureTower) {
        var closestHostile = tower.pos.findClosestByRange(FIND_HOSTILE_CREEPS)

        if (closestHostile != null) {
            tower.attack(closestHostile)
            return
        }

        var closestDamagedStructure = tower.room.find(FIND_MY_STRUCTURES).filter { x -> x.hits < x.hitsMax }.sortedBy { a -> tower.pos.getRangeTo(a.pos) }[0]
        tower.repair(closestDamagedStructure)

    }
}