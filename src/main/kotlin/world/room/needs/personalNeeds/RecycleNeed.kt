package world.room.needs.personalNeeds

import creep.fsm.states.IState
import creep.fsm.states.RecycleState
import world.room.needs.INeed

public class RecycleNeed(score: Float) : INeed(score) {
    override fun getStateToSatisfy(): IState {
        return RecycleState()
    }

}