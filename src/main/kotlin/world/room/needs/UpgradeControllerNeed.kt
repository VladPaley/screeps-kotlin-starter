package world.room.needs

import creep.fsm.states.IState
import creep.fsm.states.UpgradeControllerState

class UpgradeControllerNeed  (score: Float): INeed(score) {
    override fun getStateToSatisfy(): IState {
        return UpgradeControllerState()
    }

}