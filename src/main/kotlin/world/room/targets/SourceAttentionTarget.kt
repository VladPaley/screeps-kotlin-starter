package world.room.targets

import screeps.api.Creep
import screeps.api.Source

class SourceAttentionTarget(private val source: Source) : IAtentionTarget {

    val involvedCreeps : MutableList<Creep> = mutableListOf();

    val CREEP_SCORE_DECREACE = 100;


    override fun calculateAttentionScore(creep: Creep): Float {
        var score = 0f;

        score -= involvedCreeps.size * CREEP_SCORE_DECREACE;

        score -= creep.pos.getRangeTo(source.pos);

        return score;
    }

    override fun occupy(creep: Creep): Boolean {
       return involvedCreeps.add(creep);
    }


}