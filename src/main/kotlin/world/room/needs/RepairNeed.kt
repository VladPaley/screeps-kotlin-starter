package world.room.needs

import creep.fsm.states.IState
import creep.fsm.states.RepairSatate

class RepairNeed(score: Float) : INeed(score) {
    override fun getStateToSatisfy(): IState {
        return RepairSatate()
    }

}