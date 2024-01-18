package com.example.fivenightsescape.player

import android.location.Location
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

private const val PLAYER_HEALTH = 3

class Player(var position: LatLng, val healthBar: TextView): ViewModel() {
    var health: Int = PLAYER_HEALTH

    var location: Location = Location("")

    init {
        this.location.latitude = this.position.latitude
        this.location.longitude = this.position.longitude

        this.healthBar.text = this.health.toString()
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

        this.healthBar.text = this.health.toString()
    }
}
