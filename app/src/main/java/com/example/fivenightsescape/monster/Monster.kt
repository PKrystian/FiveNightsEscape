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

const val MONSTER_DAMAGE: Int = 1

const val MONSTER_ZERO_SPEED: Double = 0.0
const val MONSTER_CHASING_SPEED: Double = 0.0001
const val MONSTER_WANDERING_SPEED: Double = 0.00005

const val MONSTER_ZERO_RANGE: Double = 0.0
const val MONSTER_TYPE_STANDING_RANGE: Double = 10.0
const val MONSTER_TYPE_MOVING_RANGE: Double = 10.0
const val MONSTER_TYPE_WANDERING_RANGE: Double = 10.0

const val MONSTER_DEFAULT_COLOR = Color.BLACK
var MONSTER_TYPE_STANDING_COLOR = Color.rgb(130, 126, 12)
var MONSTER_TYPE_MOVING_COLOR = Color.rgb(255, 0, 0)
var MONSTER_TYPE_WANDERING_COLOR = Color.rgb(204, 111, 24)

const val MONSTER_WANDER_OFFSET: Double = 0.0005

abstract class Monster(
    val damage: Int = MONSTER_DAMAGE,
    var player: Player,
    var position: LatLng,
    private var mMap: GoogleMap)
{
    var speed: Double = MONSTER_ZERO_SPEED
    var range: Double = MONSTER_ZERO_RANGE
    var color: Int = MONSTER_DEFAULT_COLOR
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

    protected fun initCircle()
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
        this.range = MONSTER_TYPE_STANDING_RANGE
        this.color = MONSTER_TYPE_STANDING_COLOR

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
        this.speed = MONSTER_CHASING_SPEED
        this.range = MONSTER_TYPE_MOVING_RANGE
        this.color = MONSTER_TYPE_MOVING_COLOR

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
        this.speed = MONSTER_WANDERING_SPEED
        this.range = MONSTER_TYPE_WANDERING_RANGE
        this.color = MONSTER_TYPE_WANDERING_COLOR

        this.initCircle()
        this.initializeWandering()
    }

    private fun initializeWandering()
    {
        val fromLatitude = this.position.latitude - Random.nextDouble(until = MONSTER_WANDER_OFFSET)
        val fromLongitude = this.position.longitude - Random.nextDouble(until = MONSTER_WANDER_OFFSET)

        val toLatitude = this.position.latitude + Random.nextDouble(until = MONSTER_WANDER_OFFSET)
        val toLongitude = this.position.longitude + Random.nextDouble(until = MONSTER_WANDER_OFFSET)

        this.wanderFrom.latitude = fromLatitude
        this.wanderFrom.longitude = fromLongitude

        this.wanderTo.latitude = toLatitude
        this.wanderTo.longitude = toLongitude
    }
}
