package world.room

import screeps.api.*
import starter.state
import world.room.needs.*
import world.room.needs.personalNeeds.HealNeed
import world.room.needs.personalNeeds.RecycleNeed

public class RoomNeedsManager(private val room: Room) {
    val MIN_CONTROLLER_LEVEL = 2.2f;
    val PER_CREEP_DECREACE = 1
    val PERCENT_TO_START_FIXING = 0.4f;
    val MIN_TICKS_TO_LIVE = 300
    val DESIRE_TICKS_TO_LIVE = 1000
    val DESIRE_COUNT_OF_CREEPS = 6


    var needs: MutableList<INeed> = arrayListOf()

    public fun UpdateNeeds() {
        UpdateCollectNeed();
        UpdateBuildNeed();
        UpdateUpgradeNeed();
        UpdateRepairNeed();

        needs = needs.sortedByDescending { it.score }.toMutableList()
    }

    public fun GetPersonalNeed(creep: Creep): INeed? {
        var personalNeeds: MutableList<INeed> = arrayListOf()
        UpdateHealNeed(creep, personalNeeds);
        UpdateRecycleNeed(creep, personalNeeds)
        personalNeeds = personalNeeds.sortedByDescending { it.score }.toMutableList()

        return if (personalNeeds.size > 0)
            personalNeeds[0]
        else
            null
    }

    public fun PopTopNeed(): INeed {
        var desire = needs.elementAt(0);
        desire.score -= PER_CREEP_DECREACE

        if (desire.score < 0) {
            desire.score = 0f

            if (desire.getStateToSatisfy().toString() == "UpgradeControllerState")
                desire.score = 0.1f
        }

        needs.get(0).score = desire.score
        needs = needs.sortedByDescending { it.score }.toMutableList()

        return desire;
    }


    private fun UpdateCollectNeed() {
        var score = 0f;

        score += room.energyCapacityAvailable / room.energyAvailable

        score += (DESIRE_COUNT_OF_CREEPS - room.find(FIND_MY_CREEPS).size)

        if(room.find(FIND_MY_CREEPS).size == 1)
            score += 10;

        var owners = room.find(FIND_STRUCTURES).filter { x -> x.structureType == STRUCTURE_CONTAINER || x.structureType == STRUCTURE_TOWER } as List<StoreOwner>

        owners.forEach { x ->
            score += x.store.getCapacity(RESOURCE_ENERGY)!! / x.store.getUsedCapacity(RESOURCE_ENERGY)!!
        }

        if (score > 0f) {

            if (score < 1)
                score = 1f

            needs.add(CollectEnergyNeed(score))
        }
    }

    private fun UpdateUpgradeNeed() {
        var score = 3f;

        if(room.controller?.ticksToDowngrade < 3500)
            score += 1000000f;

           needs.add(UpgradeControllerNeed(score))
    }

    private fun UpdateBuildNeed() {

        val INCRECE_FOR_TOWER = 0.5f;
        val INCRECE_FOR_CONTAINER = 0.3f;
        val INCRECE_FOR_ROAD = 0.2f;
        val INCRECE_FOR_WALL = 0.2f;
        val INCRECE_FOR_EXTENTION = 0.8f;

        var score = 0f;

        score += INCRECE_FOR_TOWER
        score += INCRECE_FOR_CONTAINER
        score += INCRECE_FOR_ROAD
        score += INCRECE_FOR_WALL
        score += INCRECE_FOR_EXTENTION

        if(room.find(FIND_CONSTRUCTION_SITES).size == 0)
            return
/*
        storages.forEach { x ->
            when (x.structureType) {
                STRUCTURE_TOWER -> score += INCRECE_FOR_TOWER;
                STRUCTURE_EXTENSION -> score += INCRECE_FOR_EXTENTION;
                STRUCTURE_ROAD -> score += INCRECE_FOR_ROAD;
                STRUCTURE_CONTAINER -> score += INCRECE_FOR_CONTAINER;
                STRUCTURE_WALL -> score += INCRECE_FOR_WALL;
            }
        }
*/
        if (score > 0f)
            needs.add(BuildNeed(score))
    }

    private fun UpdateRepairNeed() {
        if(room.find(FIND_MY_CREEPS).size < DESIRE_COUNT_OF_CREEPS)
            return

        val targets = room.find(FIND_STRUCTURES).filter { x -> x.structureType != STRUCTURE_CONTROLLER && x.hits.toFloat() / x.hitsMax.toFloat() < PERCENT_TO_START_FIXING }.toTypedArray()

        if (!targets.any())
            return

        var score = 0f;

        targets.forEach { x ->
            score += x.hitsMax.toFloat() / x.hits.toFloat();
        }
        needs.add(RepairNeed(score))
    }

    private fun UpdateHealNeed(creep: Creep, personalNeeds: MutableList<INeed> = arrayListOf()) {
        if (GetCreepCost(creep) < creep.room.energyCapacityAvailable && creep.room.find(FIND_MY_CREEPS).size > 1)
            return

        if (creep.ticksToLive > DESIRE_TICKS_TO_LIVE || (creep.ticksToLive > MIN_TICKS_TO_LIVE && creep.memory.state != "HealState"))
            return

        if(creep.room.energyAvailable < 300)
            return

        var score: Float = MIN_TICKS_TO_LIVE.toFloat() * 4 / (creep.ticksToLive.toFloat())

        if (creep.ticksToLive < DESIRE_TICKS_TO_LIVE && creep.memory.state == "HealState") {
            score *= 100000
        }

        console.log("Heal: $score")

        personalNeeds.add(HealNeed(score))
    }

    private fun UpdateRecycleNeed(creep: Creep, personalNeeds: MutableList<INeed>) {
        if (GetCreepCost(creep) > creep.room.energyCapacityAvailable || creep.room.find(FIND_MY_CREEPS).isEmpty())
            return

        if(GetCreepCost(creep) < creep.room.energyAvailable / 3)
            personalNeeds.add(RecycleNeed(999f))
    }

    private fun GetCreepCost(creep: Creep): Int {
        var cost = 0;
        creep.body.forEach { x ->
            when (x.type) {
                MOVE -> cost += 50
                WORK -> cost += 100
                CARRY -> cost += 50
                ATTACK -> cost += 80
            }
        }
        return cost
    }

}