package com.example.fivenightsescape.monster

import android.location.Location
import android.os.CountDownTimer
import com.google.android.gms.maps.model.LatLng

class MonsterController {
    lateinit var monster: Monster

    private val monsterMove = object: CountDownTimer(1000, 100) {
        override fun onTick(millisUntilFinished: Long) {
            // do something
        }
        override fun onFinish() {
            actionChase(monster)
        }
    }

    private val monsterWander = object: CountDownTimer(1000, 1000) {
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

    fun monsterAction(monster: Monster)
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

    fun actionAttack(monster: Monster)
    {
        monster.player.takeDamage(monster.damage)
    }

    fun actionWander(monster: Monster)
    {
        this.monster = monster

        if (monster.location.distanceTo(monster.wanderTo) <= 10) {
            val tempFrom = monster.wanderFrom

            monster.wanderFrom = monster.wanderTo
            monster.wanderTo = tempFrom
        }

        this.monsterMove(monster, monster.wanderTo)

        monsterWander.start()
    }

    fun monsterMove(monster: Monster, location: Location)
    {
        var monsterLatitudeSpeed = monster.speed
        var monsterLongitudeSpeed = monster.speed

        if (location.latitude > monster.position.latitude) {
            monsterLatitudeSpeed *= (-1)
        }

        if (location.longitude > monster.position.longitude) {
            monsterLongitudeSpeed *= (-1)
        }

        monster.changePosition(LatLng(monster.position.latitude - monsterLatitudeSpeed, monster.position.longitude - monsterLongitudeSpeed))
    }

    fun actionChase(monster: Monster)
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