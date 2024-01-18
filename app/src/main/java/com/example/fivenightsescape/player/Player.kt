package com.example.fivenightsescape.player

import android.location.Location
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import java.lang.ref.WeakReference

private const val PLAYER_HEALTH = 3

class Player(
    var position: LatLng,
    healthBar: TextView
) {
    var health: Int = PLAYER_HEALTH
    private val healthBarRef: WeakReference<TextView> = WeakReference(healthBar)
    private val healthBar: TextView?
        get() = healthBarRef.get()

    var location: Location = Location("")

    init {
        this.location.latitude = this.position.latitude
        this.location.longitude = this.position.longitude

        this.healthBar?.text = buildString {
            append("Health: ")
            append(health)
        }
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

        this.healthBar?.text = buildString {
            append("Health: ")
            append(health)
        }
    }
}
