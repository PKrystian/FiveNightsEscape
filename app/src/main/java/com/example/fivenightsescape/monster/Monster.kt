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

private const val MONSTER_DAMAGE: Int = 1
private const val MONSTER_ZERO_DAMAGE: Int = 0

const val MONSTER_ZERO_SPEED: Double = 0.0
const val MONSTER_CHASING_SPEED: Double = 0.000003
const val MONSTER_WANDERING_SPEED: Double = 0.000005

private const val MONSTER_ZERO_RANGE: Double = 0.0
private const val MONSTER_TYPE_STANDING_RANGE: Double = 8.0
private const val MONSTER_TYPE_MOVING_RANGE: Double = 6.0
private const val MONSTER_TYPE_WANDERING_RANGE: Double = 5.0

private const val MONSTER_DEFAULT_DETECT_RANGE: Double = 100.0

private const val MONSTER_DEFAULT_COLOR = "#000000"
private const val MONSTER_TYPE_STANDING_COLOR = "#827e0c"
private const val MONSTER_TYPE_MOVING_COLOR = "#ff0000"
private const val MONSTER_TYPE_WANDERING_COLOR = "#cc6f18"

private const val MONSTER_WANDER_OFFSET: Double = 0.0005

open class Monster(
    var damage: Int = MONSTER_DAMAGE,
    var player: Player,
    var position: LatLng,
    private var mMap: GoogleMap)
{
    var speed: Double = MONSTER_ZERO_SPEED
    var range: Double = MONSTER_ZERO_RANGE
    var detectRange: Double = MONSTER_ZERO_RANGE
    var color: String = MONSTER_DEFAULT_COLOR
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
            .strokeColor(Color.parseColor(this.color))
        )
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
            .strokeColor(Color.parseColor(this.color))
        )
    }

    fun attackPlayer()
    {
        this.player.takeDamage(this.damage)
        this.damage = MONSTER_ZERO_DAMAGE
        this.circle.remove()
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
        this.detectRange = this.range
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
        this.detectRange = MONSTER_DEFAULT_DETECT_RANGE
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
        this.detectRange = MONSTER_DEFAULT_DETECT_RANGE
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
