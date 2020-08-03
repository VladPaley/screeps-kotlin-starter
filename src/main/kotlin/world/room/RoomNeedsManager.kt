package world.room

import creep.fsm.states.RepairSatate
import screeps.api.*
import screeps.api.structures.Structure
import starter.state
import world.room.needs.*

public class RoomNeedsManager(private val room: Room) {
    val MIN_CONTROLLER_LEVEL = 2.2f;
    val PER_CREEP_DECREACE = 1
    val PERCENT_TO_START_FIXING = 0.5f;
    val MIN_TICKS_TO_LIVE = 300
    val DESIRE_TICKS_TO_LIVE = 1000


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
           needs.add(UpgradeControllerNeed(3f))
    }

    private fun UpdateBuildNeed() {

        val INCRECE_FOR_TOWER = 0.5f;
        val INCRECE_FOR_CONTAINER = 0.3f;
        val INCRECE_FOR_ROAD = 0.2f;
        val INCRECE_FOR_WALL = 0.2f;
        val INCRECE_FOR_EXTENTION = 0.8f;

        var score = 0f;

        val storages = room.find(FIND_CONSTRUCTION_SITES);

        storages.forEach { x ->
            when (x.structureType) {
                STRUCTURE_TOWER -> score += INCRECE_FOR_TOWER;
                STRUCTURE_EXTENSION -> score += INCRECE_FOR_EXTENTION;
                STRUCTURE_ROAD -> score += INCRECE_FOR_ROAD;
                STRUCTURE_CONTAINER -> score += INCRECE_FOR_CONTAINER;
                STRUCTURE_WALL -> score += INCRECE_FOR_WALL;
            }
        }

        if (score > 0f)
            needs.add(BuildNeed(score))
    }

    private fun UpdateRepairNeed() {
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

        var spawn = creep.room.find(FIND_MY_SPAWNS)[0]
        var score: Float = MIN_TICKS_TO_LIVE.toFloat() * 4 / (creep.ticksToLive.toFloat()) - (spawn.store.getCapacity(RESOURCE_ENERGY)?.toFloat()?.div((spawn.store.getUsedCapacity(RESOURCE_ENERGY)!!.toFloat().plus(0.1f)))
                ?: 100f)

        if (creep.ticksToLive < DESIRE_TICKS_TO_LIVE && creep.memory.state == "HealState") {
            score *= 100000
        }

        personalNeeds.add(HealNeed(score))
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