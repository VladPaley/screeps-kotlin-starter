package world.room.needs

import creep.fsm.states.IState

abstract class INeed(var score: Float) {
    abstract fun getStateToSatisfy() : IState

}