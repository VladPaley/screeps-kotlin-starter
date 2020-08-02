package world.room.needs

import creep.fsm.states.HealState
import creep.fsm.states.IState

class HealNeed (score: Float): INeed(score)  {
    override fun getStateToSatisfy(): IState {
        return HealState()
    }
}