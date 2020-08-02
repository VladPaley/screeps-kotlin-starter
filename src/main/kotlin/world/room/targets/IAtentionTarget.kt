package world.room.targets

import screeps.api.Creep

interface IAtentionTarget {
    fun calculateAttentionScore(creep: Creep) : Float;
    fun occupy(creep: Creep) : Boolean;
}