package world.room.targets

import screeps.api.FIND_SOURCES
import screeps.api.Room
import screeps.api.Source


class RoomBlackboard {
    val attentionTargets = listOf<IAtentionTarget>();

    public fun UpdateAttentionTargets(room: Room) {

    }

    private fun GetSources(room: Room): Array<Source> {
        return room.find(FIND_SOURCES)
    }
}