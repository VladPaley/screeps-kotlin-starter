package creep.fsm

import creep.fsm.states.*
import screeps.api.Creep
import starter.state

class FSM {

    fun Operate(creep: Creep, state: IState) {
        var currentState = GetState(creep.memory.state)

        if (currentState != null && state.toString() != creep.memory.state) {
            currentState.Exit(creep)
            state.Enter(creep)
        }

        state.Execute(creep);

    }

    fun GetState(state: String): IState? {
        when (state) {
            "HealState" -> return HealState()
            //          "WithdrawEnergyState" -> return WithdrawEnergyState()
            "BuildState" -> return BuildState()
            "RepairSatate" -> return RepairSatate()
            //      "DelivedState" -> return DelivedState()
        }

        return null
    }
}