package helper

import screeps.api.RoomPosition
import kotlin.math.roundToInt
import kotlin.random.Random

class MathHelper {
    fun GetRandomPointOnCircleInRoom(radius : Int, roomName: String, roomPosition: RoomPosition) : RoomPosition{
        var anale = Random.nextFloat() * kotlin.math.PI * 2
        var x = kotlin.math.sin(anale) * radius
        var y = kotlin.math.cos(anale) * radius

        return RoomPosition(roomPosition.x + x.roundToInt(), roomPosition.y + y.roundToInt(), roomName);
    }
}