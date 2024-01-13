package com.example.fivenightsescape.monster

import android.graphics.Color
import android.location.Location
import com.example.fivenightsescape.player.Player
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import kotlin.random.Random

const val TYPE_STANDING = "standing"
const val TYPE_MOVING = "moving"
const val TYPE_WANDERING = "wandering"

abstract class Monster(
    val damage: Int = 1,
    var player: Player,
    var position: LatLng,
    var mMap: GoogleMap)
{
    var speed: Double = 0.0
    var range: Double = 10.0
    var color: Int = Color.BLACK
    var monsterType: String = ""
    var location: Location = Location("")

    // Only for MonsterWandering
    var wanderFrom: Location = Location("")
    var wanderTo: Location = Location("")

    private lateinit var circle: Circle

    init {
        this.location.latitude = this.position.latitude
        this.location.longitude = this.position.longitude
    }

    fun initCircle()
    {
        this.circle = mMap.addCircle(CircleOptions()
            .center(this.position)
            .radius(this.range)
            .strokeColor(this.color)
            .fillColor(this.color))
    }

    fun changePosition(position: LatLng)
    {
        this.position = position

        this.location.latitude = position.latitude
        this.location.longitude = position.longitude

        this.circle.remove()
        this.circle = mMap.addCircle(CircleOptions()
            .center(this.position)
            .radius(this.range)
            .strokeColor(this.color)
            .fillColor(this.color))
    }

    fun attackPlayer()
    {
        this.player.takeDamage(this.damage)
    }
}

class MonsterStanding(
    damage: Int = 1,
    player: Player,
    position: LatLng,
    mMap: GoogleMap
): Monster(damage, player, position, mMap) {
    init {
        this.monsterType = TYPE_STANDING
        this.range = 10.0
        this.color = Color.rgb(130, 126, 12)

        this.initCircle()
    }
}

class MonsterMoving(
    damage: Int = 1,
    player: Player,
    position: LatLng,
    mMap: GoogleMap
): Monster(damage, player, position, mMap) {
    init {
        this.monsterType = TYPE_MOVING
        this.speed = 0.0001
        this.range = 20.0
        this.color = Color.RED

        this.initCircle()
    }
}

class MonsterWandering(
    damage: Int = 1,
    player: Player,
    position: LatLng,
    mMap: GoogleMap
): Monster(damage, player, position, mMap) {
    init {
        this.monsterType = TYPE_WANDERING
        this.speed = 0.0001
        this.range = 15.0
        this.color = Color.rgb(204, 111, 24)

        this.initCircle()
        this.initializeWandering()
    }

    fun initializeWandering()
    {
        val fromLatitude = this.position.latitude - Random.nextDouble(until = 0.0005)
        val fromLongitude = this.position.longitude - Random.nextDouble(until = 0.0005)

        val toLatitude = this.position.latitude + Random.nextDouble(until = 0.0005)
        val toLongitude = this.position.longitude + Random.nextDouble(until = 0.0005)

        this.wanderFrom.latitude = fromLatitude
        this.wanderFrom.longitude = fromLongitude

        this.wanderTo.latitude = toLatitude
        this.wanderTo.longitude = toLongitude
    }
}
