package world.room.needs

import creep.fsm.states.CollectState
import creep.fsm.states.IState

class CollectEnergyNeed (score: Float): INeed(score) {
    override fun getStateToSatisfy(): IState {
        return CollectState()
    }
}