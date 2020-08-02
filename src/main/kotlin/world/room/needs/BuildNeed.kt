package world.room.needs

import creep.fsm.states.BuildState
import creep.fsm.states.IState


class BuildNeed (score: Float): INeed(score) {
    override fun getStateToSatisfy(): IState {
        return BuildState()
    }
}