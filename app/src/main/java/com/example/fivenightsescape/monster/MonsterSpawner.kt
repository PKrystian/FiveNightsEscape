package com.example.fivenightsescape.monster

import android.content.Context
import com.example.fivenightsescape.EASY
import com.example.fivenightsescape.HARD
import com.example.fivenightsescape.MEDIUM
import com.example.fivenightsescape.player.Player
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.random.Random

private const val EASY_SPAWNER_BASIC = 7
private const val EASY_SPAWNER_MODIFIER = 2

private const val MEDIUM_SPAWNER_BASIC = 5
private const val MEDIUM_SPAWNER_MODIFIER = 3

private const val HARD_SPAWNER_BASIC = 3
private const val HARD_SPAWNER_MODIFIER = 4

private const val DICE_MIN = 1
private const val DICE_MAX = 11

private const val MINIMUM_DISTANCE_FROM_PLAYER = 2
private const val LOCALIZATION_MIN = -9
private const val LOCALIZATION_MAX = 10

private const val DIVIDER = 10000

class MonsterSpawner(
    private val player: Player,
    private val mMap: GoogleMap,
    private val difficulty: String?,
    private val context: Context?
) {
    private val monsterController = MonsterController()

    init{
        context?.let {monsterController.setContext(context) } //getting context for the toast alert
    }

    fun addMonster()
    {
        val dice: Int = Random.nextInt(DICE_MIN, DICE_MAX)

        when (this.difficulty) {
            EASY -> spawnMonsterForDifficulty(dice, EASY_SPAWNER_BASIC, EASY_SPAWNER_MODIFIER)
            MEDIUM -> spawnMonsterForDifficulty(dice, MEDIUM_SPAWNER_BASIC, MEDIUM_SPAWNER_MODIFIER)
            HARD -> spawnMonsterForDifficulty(dice, HARD_SPAWNER_BASIC, HARD_SPAWNER_MODIFIER)
            else -> {
                // do some log error
            }
        }
    }

    fun updateMonsters()
    {
        this.monsterController.monsterAlert()
    }

    private fun spawnMonster(monsterType: String)
    {
        val randomLatitude = randomPosition()
        val randomLongitude = randomPosition()

        when (monsterType) {
            TYPE_STANDING -> {
                this.monsterController.addMonster(MonsterStanding(
                    player = player,
                    position = LatLng(
                        player.position.latitude - randomLatitude,
                        player.position.longitude - randomLongitude
                    ),
                    mMap = this.mMap
                ))
            }
            TYPE_MOVING -> {
                this.monsterController.addMonster(MonsterMoving(
                    player = player,
                    position = LatLng(
                        player.position.latitude - randomLatitude,
                        player.position.longitude - randomLongitude
                    ),
                    mMap = this.mMap
                ))
            }
            TYPE_WANDERING -> {
                this.monsterController.addMonster(MonsterWandering(
                    player = player,
                    position = LatLng(
                        player.position.latitude - randomLatitude,
                        player.position.longitude - randomLongitude
                    ),
                    mMap = this.mMap
                ))
            }
        }
        this.monsterController.monsterAlert()
    }

    private fun spawnMonsterForDifficulty(dice: Int, basicThreshold: Int, modifier: Int) {
        when {
            dice < basicThreshold -> spawnMonster(TYPE_STANDING)
            dice < basicThreshold + modifier -> spawnMonster(TYPE_MOVING)
            dice < basicThreshold + modifier + modifier -> spawnMonster(TYPE_WANDERING)
            else -> {
                // do some log error
            }
        }
    }

    private fun randomPosition(): Double
    {
        var randomNumber = 0

        while (abs(randomNumber) <= MINIMUM_DISTANCE_FROM_PLAYER) {
            randomNumber = Random.nextInt(LOCALIZATION_MIN, LOCALIZATION_MAX)
        }
        return randomNumber.toDouble() / DIVIDER
    }

    fun despawnAll()
    {
        this.monsterController.killAll()
    }
}
