package world.room

import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.utils.unsafe.jsObject
import starter.Role
import starter.role

class PopulationManager(private val spawn: StructureSpawn) {
    val DESIRE_WORKERS_COUNT = 4;
    val DESIRE_WARRIORS_COUNT = 0;

    public fun Process(room: Room) {
        var creepsCount = spawn.room.find(FIND_MY_CREEPS).size;

        var actuallWorkersCount = DESIRE_WORKERS_COUNT + room.find(FIND_SOURCES).size

        if (creepsCount >= actuallWorkersCount)
            return;

        spawn.spawnCreep(
                GetBodyForWorker(),
                "Worker_${Game.time}",
                options {
                    memory = jsObject<CreepMemory> {
                        this.role = Role.HARVESTER
                    }
                }
        )
    }

    private fun GetBodyForWorker(): Array<BodyPartConstant> {


        var avaliblePoints = spawn.room.energyCapacityAvailable;

        if(spawn.room.find(FIND_MY_CREEPS).size == 0)
            avaliblePoints = spawn.room.energyAvailable

        if(spawn.room.find(FIND_MY_CREEPS).size == 1)
            avaliblePoints /= 2

        val avalibleBaseBlocks: Int = avaliblePoints / 300

        var result: MutableList<BodyPartConstant> = mutableListOf();

        for (i in 0..avalibleBaseBlocks - 1) {
            result.add(WORK)
            result.add(WORK)
            result.add(MOVE)
            result.add(CARRY)

            avaliblePoints -= 300;
        }

        if (avaliblePoints >= 100) {
            result.add(MOVE)
            result.add(CARRY)
            avaliblePoints -= 100;
        }

        while (avaliblePoints >= 150) {
            result.add(WORK)
            result.add(CARRY)
            avaliblePoints-= 150;
        }

        if(avaliblePoints == 100) {
            result.add(MOVE)
            result.add(CARRY)
        }

        if(avaliblePoints == 50)
            result.add(MOVE)


        return result.toTypedArray()
    }

}