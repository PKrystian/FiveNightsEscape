package com.example.fivenightsescape.monster

import android.location.Location
import android.os.CountDownTimer
import com.google.android.gms.maps.model.LatLng

private const val DEFAULT_COUNTDOWN_INTERVAL_MOVING: Long = 250
private const val DEFAULT_COUNT_FROM: Long = 500

private const val DEFAULT_COUNTDOWN_INTERVAL_WANDERING: Long = 1000

private const val DEFAULT_WANDERING_DISTANCE = 10


class MonsterController {
    private val monsters: MutableList<Monster> = mutableListOf()
    private val monsterMoveTimers: MutableMap<Monster, CountDownTimer> = mutableMapOf()
    private val monsterWanderTimers: MutableMap<Monster, CountDownTimer> = mutableMapOf()

    private fun createMonsterMoveTimer(monster: Monster): CountDownTimer {
        return object : CountDownTimer(DEFAULT_COUNT_FROM, DEFAULT_COUNTDOWN_INTERVAL_MOVING) {
            override fun onTick(millisUntilFinished: Long) {
                // do something
            }

            override fun onFinish() {
                actionChase(monster)
            }
        }
    }

    private fun createMonsterWanderTimer(monster: Monster): CountDownTimer {
        return object : CountDownTimer(DEFAULT_COUNT_FROM, DEFAULT_COUNTDOWN_INTERVAL_WANDERING) {
            override fun onTick(millisUntilFinished: Long) {
                // do something
            }

            override fun onFinish() {
                actionWander(monster)
            }
        }
    }

    fun addMonster(monster: Monster) {
        monsters.add(monster)

        when (monster.monsterType) {
            TYPE_WANDERING -> {
                val wanderTimer = createMonsterWanderTimer(monster)
                monsterWanderTimers[monster] = wanderTimer
                wanderTimer.start()
            }
        }
    }

    fun monsterAlert() {
        for (monster in this.monsters) {
            if (monster.location.distanceTo(monster.player.location) <= monster.range) {
                actionAttack(monster)
            }
            if (monster.location.distanceTo(monster.player.location) <= monster.detectRange) {
                monsterAction(monster)
            }
        }
    }

    private fun monsterAction(monster: Monster) {
        when (monster.monsterType) {
            TYPE_STANDING -> {
                actionAttack(monster)
            }
            TYPE_MOVING -> {
                monster.speed = MONSTER_CHASING_SPEED
                actionChase(monster)
            }
            TYPE_WANDERING -> {
                monster.speed = MONSTER_CHASING_SPEED
                monsterWanderTimers[monster]?.cancel()
                actionChase(monster)
            }
        }
    }

    private fun actionAttack(monster: Monster) {
        monster.attackPlayer()
        this.monsters.remove(monster)
    }

    private fun actionWander(monster: Monster) {
        if (monster.location.distanceTo(monster.wanderTo) <= DEFAULT_WANDERING_DISTANCE) {
            val tempFrom = monster.wanderFrom
            monster.wanderFrom = monster.wanderTo
            monster.wanderTo = tempFrom
        }

        monsterMove(monster, monster.wanderTo)

        val wanderTimer = createMonsterWanderTimer(monster)
        monsterWanderTimers[monster] = wanderTimer
        wanderTimer.start()
    }

    private fun monsterMove(monster: Monster, location: Location) {
        var monsterLatitudeSpeed = monster.speed
        var monsterLongitudeSpeed = monster.speed

        if (location.latitude > monster.position.latitude) {
            monsterLatitudeSpeed *= -1
        }

        if (location.longitude > monster.position.longitude) {
            monsterLongitudeSpeed *= -1
        }

        monster.changePosition(
            LatLng(
                monster.position.latitude - monsterLatitudeSpeed,
                monster.position.longitude - monsterLongitudeSpeed
            )
        )
    }

    private fun actionChase(monster: Monster) {
        monsterMove(monster, monster.player.location)

        if (monster.location.distanceTo(monster.player.location) <= monster.range) {
            monsterMoveTimers[monster]?.cancel()
            this.actionAttack(monster)
            return
        }

        val moveTimer = createMonsterMoveTimer(monster)
        monsterMoveTimers[monster] = moveTimer
        moveTimer.start()
    }
}
