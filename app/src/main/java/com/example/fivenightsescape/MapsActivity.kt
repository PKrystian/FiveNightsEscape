package com.example.fivenightsescape

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import android.os.Handler
import android.widget.ProgressBar
import android.widget.TextView
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fivenightsescape.monster.MonsterSpawner
import com.example.fivenightsescape.player.Player
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

private const val REQUEST_ACCESS_FINE_LOCATION = 1
private const val DEFAULT_ZOOM_LEVEL = 15f
private const val TOAST_TEXT_ERROR = "Unable to get current location"
private const val TOAST_TEXT_DENIED = "Location permission denied"
private const val DEFAULT_DELAY: Long = 500
private const val DEFAULT_LEVEL_CHANGER = 4
private const val DEFAULT_LEVEL_SLOWING = 2
private const val DEFAULT_SPEED_PROGRESSION = 2
private const val DEFAULT_LEVEL_PROGRESSION = 1
private const val PERCENT = 100
private const val MONSTER_SPAWN_DELAY: Long = 10000
private const val PLAYER_HEALTH_MONITOR_INTERVAL: Long = 500
private const val INTERVAL = 50
private const val FASTEST_INTERVAL = 25

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var player: Player
    private lateinit var monsterSpawner: MonsterSpawner

    private var initialized: Boolean = false

    //timer and lv things
    private lateinit var timerONE :Chronometer

    //levelChanger * delay = every how many milliseconds next lv, it changes every two levels
    private var levelChanger = DEFAULT_LEVEL_CHANGER
    private val delay = DEFAULT_DELAY
    private var currentLevel = 1 //lv the player is on
    private var subLevel = 0 // for the progressbar and to change level length
    private val handlerTimer = Handler(Looper.getMainLooper())

    private lateinit var levelText: TextView
    private lateinit var progressBar: ProgressBar

    private var currentPlayerLocation: LatLng? = null

    private fun monitorPlayerHealth(): CountDownTimer {
        return object : CountDownTimer(PLAYER_HEALTH_MONITOR_INTERVAL, PLAYER_HEALTH_MONITOR_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                // do something
            }

            override fun onFinish() {
                if (player.health <= 0) {
                    gameOver()
                    return
                }

                monitorPlayerHealth().start()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        //timer
        timerONE = findViewById(R.id.timerONE)
        timerONE.start()
        handlerTimer.postDelayed(newLevel, delay)
        //level indicator
        levelText = findViewById(R.id.levelIndicator)
        levelText.text = getString(R.string.level, currentLevel)
        //progress bar
        progressBar = findViewById(R.id.progressBar)

        val stopButton: Button = findViewById(R.id.stopButton)

        stopButton.setOnClickListener {
            this.purgeEverything()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    //function to change levels
    private val newLevel = object : Runnable {
        override fun run() {
            runOnUiThread {
                if (subLevel<levelChanger){
                    subLevel += DEFAULT_LEVEL_PROGRESSION
                    progressBar.progress = (subLevel.toDouble() / levelChanger * PERCENT).toInt()
                }
                else{
                    if (currentLevel % DEFAULT_LEVEL_SLOWING == 0){
                        levelChanger *= DEFAULT_SPEED_PROGRESSION
                    // every x levels it takes y times longer to get another lv
                    }
                    progressBar.progress = 0
                    subLevel = 0
                    currentLevel += DEFAULT_LEVEL_PROGRESSION
                    levelText.text = getString(R.string.level, currentLevel)
                }
            }
            handlerTimer.postDelayed(this, delay)
        }
    }

    private fun gameInit(currentLatLng: LatLng) {
        this.player = Player(
            position = currentLatLng,
            healthBar = this.findViewById(R.id.healthIndicator)
        )

        this.monsterSpawner = MonsterSpawner(
            player = this.player,
            mMap = mMap,
            difficulty = intent.getStringExtra(INTENT_EXTRA),
            Context = this
        )

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed(object : Runnable {
            override fun run() {
                currentPlayerLocation?.let {
                    monsterSpawner.addMonster()
                }

                handler.postDelayed(this, MONSTER_SPAWN_DELAY)
            }
        }, MONSTER_SPAWN_DELAY)

        this.monitorPlayerHealth().start()
        this.initialized = true
    }
    override fun onMapReady(
        googleMap: GoogleMap
    ) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        val locationRequest = LocationRequest.create().apply {
            INTERVAL
            FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations){
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        currentPlayerLocation = currentLatLng

                        if (!initialized) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))
                            gameInit(currentLatLng)
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                        }

                        player.changePosition(currentLatLng)

                        monsterSpawner.updateMonsters()
                    } else {
                        Toast.makeText(this@MapsActivity, TOAST_TEXT_ERROR, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap)
            } else {
                Toast.makeText(this, TOAST_TEXT_DENIED, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun gameOver() {
        this.purgeEverything()

        val intent = Intent(this@MapsActivity, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun purgeEverything() {
        this.timerONE.stop() //stops timer
        this.handlerTimer.removeCallbacks(newLevel) //stops level counting

        this.monitorPlayerHealth().cancel()

        this.monsterSpawner.despawnAll()
    }
}
