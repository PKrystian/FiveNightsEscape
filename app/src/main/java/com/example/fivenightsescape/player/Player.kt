package com.example.fivenightsescape.player

import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

const val PLAYER_HEALTH = 3

class Player(var position: LatLng): ViewModel() {
    private var health: Int = PLAYER_HEALTH
    var location: Location = Location("")

    init {
        this.location.latitude = this.position.latitude
        this.location.longitude = this.position.longitude
    }

    fun changePosition(position: LatLng)
    {
        this.position = position

        this.location.latitude = position.latitude
        this.location.longitude = position.longitude
    }

    fun takeDamage(damage: Int = 1)
    {
        this.health -= damage

        if (this.health <= 0) {
            this.death()
        }
    }

    private fun death()
    {
        // Implement GAME OVER Screen etc.
    }
}
