package world.room.needs.personalNeeds

import creep.fsm.states.HealState
import creep.fsm.states.IState
import world.room.needs.INeed

class HealNeed (score: Float): INeed(score)  {
    override fun getStateToSatisfy(): IState {
        return HealState()
    }
}