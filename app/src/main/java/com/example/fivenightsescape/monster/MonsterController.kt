package com.example.fivenightsescape.monster

import android.location.Location
import android.os.CountDownTimer
import com.google.android.gms.maps.model.LatLng

const val DEFAULT_COUNTDOWN_INTERVAL_MOVING: Long = 100
const val DEFAULT_COUNTFROM: Long = 1000

const val DEFAULT_COUNTDOWN_INTERVAL_WANDERING: Long = 1000

const val DEFAULT_WANDERING_DISTANCE = 10

class MonsterController {
    lateinit var monster: Monster

    private val monsterMove = object: CountDownTimer(
        DEFAULT_COUNTFROM,
        DEFAULT_COUNTDOWN_INTERVAL_MOVING
    ) {
        override fun onTick(millisUntilFinished: Long) {
            // do something
        }
        override fun onFinish() {
            actionChase(monster)
        }
    }

    private val monsterWander = object: CountDownTimer(
        DEFAULT_COUNTFROM,
        DEFAULT_COUNTDOWN_INTERVAL_WANDERING
    ) {
        override fun onTick(millisUntilFinished: Long) {
            // do something
        }
        override fun onFinish() {
            actionWander(monster)
        }
    }

    fun monsterActivate(monster: Monster)
    {
        this.monster = monster

        when (monster.monsterType) {
            TYPE_WANDERING -> {
                monsterWander.start()
            }
        }
    }

    fun monsterAlert(monster: Monster)
    {
        this.monster = monster

        if (monster.location.distanceTo(monster.player.location) <= monster.range) {
            monsterAction(monster)
        }
    }

    private fun monsterAction(monster: Monster)
    {
        this.monster = monster

        when (monster.monsterType) {
            TYPE_STANDING -> {
                actionAttack(monster)
            }
            TYPE_MOVING -> {
                actionChase(monster)
            }
            TYPE_WANDERING -> {
                monsterWander.cancel()
                actionChase(monster)
            }
        }
    }

    private fun actionAttack(monster: Monster)
    {
        monster.player.takeDamage(monster.damage)
    }

    private fun actionWander(monster: Monster)
    {
        this.monster = monster

        if (monster.location.distanceTo(monster.wanderTo) <= DEFAULT_WANDERING_DISTANCE) {
            val tempFrom = monster.wanderFrom

            monster.wanderFrom = monster.wanderTo
            monster.wanderTo = tempFrom
        }

        this.monsterMove(monster, monster.wanderTo)

        monsterWander.start()
    }

    private fun monsterMove(monster: Monster, location: Location)
    {
        var monsterLatitudeSpeed = monster.speed
        var monsterLongitudeSpeed = monster.speed

        if (location.latitude > monster.position.latitude) {
            monsterLatitudeSpeed *= (-1)
        }

        if (location.longitude > monster.position.longitude) {
            monsterLongitudeSpeed *= (-1)
        }

        monster.changePosition(LatLng(
            monster.position.latitude - monsterLatitudeSpeed,
            monster.position.longitude - monsterLongitudeSpeed)
        )
    }

    private fun actionChase(monster: Monster)
    {
        this.monster = monster

        this.monsterMove(monster, monster.player.location)

        if (monster.location.distanceTo(monster.player.location) <= monster.range) {
            monsterMove.cancel()
            return
        }

        monsterMove.start()
    }
}
