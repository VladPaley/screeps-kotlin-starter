package world.room

import helper.MathHelper
import screeps.api.*

class RoomBuilder {
    val ExtentionSpreadRadius = 1
    val RoadSpreadRadius = 0
    val TowerSpreadRadius = 4
    val MaxTrackRoads = 5;
    val ContainersPerControllerLevel = 0.25f;

    public fun Operate(room: Room) {

        BuildAtRadomPosGreed(room, STRUCTURE_EXTENSION, ExtentionSpreadRadius + (room.controller?.level ?: 0), 0)
        BuildAtRadomPosGreed(room, STRUCTURE_ROAD, RoadSpreadRadius, 1)
        BuildAtRadomPosCicle(room, STRUCTURE_TOWER, TowerSpreadRadius)
        BuildAtRadomPosCicle(room, STRUCTURE_CONTAINER, (ContainersPerControllerLevel * (room.controller?.level
                ?: 0)).toInt())
        BuildRoadsToSources(room)
        BuildSourcesRoads(room)
    }

    private fun BuildSourcesRoads(room: Room) {
        var sources = room.find(FIND_SOURCES);

        sources.forEach { source ->
            room.createConstructionSite(source.pos.x + 1, source.pos.y, STRUCTURE_ROAD)
            room.createConstructionSite(source.pos.x + 1, source.pos.y + 1, STRUCTURE_ROAD);
            room.createConstructionSite(source.pos.x + 1, source.pos.y - 1, STRUCTURE_ROAD);
            room.createConstructionSite(source.pos.x - 1, source.pos.y + 1, STRUCTURE_ROAD);
            room.createConstructionSite(source.pos.x - 1, source.pos.y - 1, STRUCTURE_ROAD);
            room.createConstructionSite(source.pos.x - 1, source.pos.y, STRUCTURE_ROAD);
            room.createConstructionSite(source.pos.x, source.pos.y + 1, STRUCTURE_ROAD);
            room.createConstructionSite(source.pos.x, source.pos.y - 1, STRUCTURE_ROAD);
        }
    }

    private fun BuildAtRadomPosGreed(room: Room, structureConstant: BuildableStructureConstant, radius: Int, seed: Int, amount: Int = 1000) {

        if ((room.find(FIND_STRUCTURES).filter { x -> x.structureType == structureConstant }.size + room.find(FIND_CONSTRUCTION_SITES).filter { x -> x.structureType == structureConstant }.size) >= amount)
            return

        var spawn = room.find(FIND_MY_SPAWNS)[0]
        var radiusUpdated = radius + (room.controller?.level ?: 0)

        for (i in -radiusUpdated..radiusUpdated step 2)
            for (j in -radiusUpdated..radiusUpdated step 2) {
                var point = RoomPosition(spawn.pos.x + i + seed, spawn.pos.y + j + seed, room.name)
                var occupied = false

                point.look().forEach { x ->
                    if (x.terrain?.value == "wall")
                        occupied = true;
                    if (x.structure != null)
                        occupied = true
                }

                if (occupied)
                    continue

                room.createConstructionSite(point, structureConstant)
            }
    }

    private fun BuildAtRadomPosCicle(room: Room, structureConstant: BuildableStructureConstant, radius: Int, amount: Int = 1000) {

        if ((room.find(FIND_STRUCTURES).filter { x -> x.structureType == structureConstant }.size + room.find(FIND_CONSTRUCTION_SITES).filter { x -> x.structureType == structureConstant }.size) >= amount)
            return

        var spawn = room.find(FIND_MY_SPAWNS)[0]
        var radiusUpdated = radius + (room.controller?.level ?: 0)

        var point = MathHelper().GetRandomPointOnCircleInRoom(radiusUpdated, room.name, spawn.pos)
        room.createConstructionSite(point, structureConstant)

    }


    fun BuildRoadsToSources(room: Room) {
        if (room.find(FIND_CONSTRUCTION_SITES).filter { x -> x.structureType.equals(STRUCTURE_ROAD) }.size > MaxTrackRoads)
            return

        var sources = room.find(FIND_SOURCES)
        var spawn = room.find(FIND_MY_SPAWNS)[0]

        sources.sort { a, b ->
            a.pos.getRangeTo(spawn) - b.pos.getRangeTo(spawn)
        }

        sources.forEach { source ->
            var path = PathFinder.search(source.pos, spawn.pos)
            path.path.reverse()
            path.path.forEach { point ->
                if (room.createConstructionSite(point, STRUCTURE_ROAD) == OK)
                    return
            }
        }
    }
}