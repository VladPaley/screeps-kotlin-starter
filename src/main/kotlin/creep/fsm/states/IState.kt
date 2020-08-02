package creep.fsm.states

import screeps.api.Creep

interface IState {
    fun Execute(creep: Creep) : Boolean
    fun Exit(creep: Creep);
    fun Enter(creep: Creep);
}