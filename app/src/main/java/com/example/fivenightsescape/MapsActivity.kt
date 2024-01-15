package com.example.fivenightsescape

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.example.fivenightsescape.monster.Monster
import com.example.fivenightsescape.monster.MonsterController
import com.example.fivenightsescape.monster.MonsterMoving
import com.example.fivenightsescape.monster.MonsterStanding
import com.example.fivenightsescape.monster.MonsterWandering
import com.example.fivenightsescape.player.Player
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.random.Random

private const val REQUEST_ACCESS_FINE_LOCATION = 1
private const val DEFAULT_ZOOM_LEVEL = 15f
private const val MARKER_TITLE = "Your Location"
private const val TOAST_TEXT_ERROR = "Unable to get current location"
private const val TOAST_TEXT_DENIED = "Location permission denied"
private const val DEFAULT_DELAY : Long = 500
private const val DEFAULT_LEVEL_CHANGER = 4
private const val DEFAUL_LEVEL_SLOWERING = 2
private const val DEFAULT_SPEED_PROGRESSION = 2
private const val DEFAULT_LEVEL_PROGRESSION = 1
private const val PROCENT = 100

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var player: Player
    private lateinit var monster: Monster

    private var monsterController: MonsterController = MonsterController()

    private var initialized: Boolean = false

    //timer and lv things
    //levelChanger * delay = every how many milliseconds next lv, it changes every two levels
    private var levelChanger = DEFAULT_LEVEL_CHANGER
    private val delay = DEFAULT_DELAY
    private var currentLevel = 1 //lv the player is on
    private var subLevel = 0 // for the progressbar and to change level length
    private val handlerTimer = Handler(Looper.getMainLooper())
    private lateinit var levelText: TextView
    private lateinit var progressBar: ProgressBar
    private var currentPlayerLocation: LatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        //timer
        val timerONE :Chronometer = findViewById(R.id.timerONE)
        timerONE.start()
        handlerTimer.postDelayed(newLevel, delay)
        //level indicator
        levelText = findViewById(R.id.levelIndicator)
        levelText.text = getString(R.string.level, currentLevel)
        //progress bar
        progressBar = findViewById(R.id.progressBar)

        val stopButton: Button = findViewById(R.id.stopButton)

        stopButton.setOnClickListener {
            timerONE.stop() //stops timer
            handlerTimer.removeCallbacks(newLevel) //stops level counting
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
                    progressBar.progress = (subLevel.toDouble() / levelChanger * PROCENT).toInt()
                }
                else{
                    if (currentLevel % DEFAUL_LEVEL_SLOWERING == 0){
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

    private fun gameInit(currentLatLng: LatLng)
    {
        this.player = Player(position = currentLatLng)

        this.monster = MonsterMoving(
            player = player,
            position = LatLng(this.player.position.latitude + 0.0002, this.player.position.longitude - 0.001), // Values in 0.0001 or lower because of scale
            mMap = mMap
        )
        this.monsterController.monsterActivate(this.monster)

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed(object : Runnable {
            override fun run() {
                currentPlayerLocation?.let { location ->
                    // Call your function with the current player location
                    monsterSpawner(location)
                }

                handler.postDelayed(this, 10000)
            }
        }, 10000)

        this.initialized = true
    }

    private fun monsterSpawner(playerLocation: LatLng)
    {
        this.player = Player(position = playerLocation)
        var dice: Int = Random.nextInt(1, 11)
        var randomLatitude = randomPosition()
        var randomLongitude = randomPosition()
        val selectedDifficulty = intent.getStringExtra("selectedDifficulty")
        Toast.makeText(this, "Selected Difficulty: $randomLongitude", Toast.LENGTH_SHORT).show()
        if(selectedDifficulty == "Easy")
        {
            if(dice < 7) {
                this.monster = MonsterStanding(
                    player = player,
                    position = LatLng(
                        this.player.position.latitude - randomLatitude,
                        this.player.position.longitude - randomLongitude
                    ), // Values in 0.0001 or lower because of scale
                    mMap = mMap
                )
            }
            else if(dice <9)
            {
                this.monster = MonsterWandering(
                    player = player,
                    position = LatLng(
                        this.player.position.latitude - randomLatitude,
                        this.player.position.longitude - randomLongitude
                    ), // Values in 0.0001 or lower because of scale
                    mMap = mMap
                )
            }
            else if(dice<11)
            {
                this.monster = MonsterMoving(
                    player = player,
                    position = LatLng(
                        this.player.position.latitude - randomLatitude,
                        this.player.position.longitude - randomLongitude
                    ), // Values in 0.0001 or lower because of scale
                    mMap = mMap
                )
            }
        }
        else if(selectedDifficulty == "Medium")
        {
            if(dice < 5) {
                this.monster = MonsterStanding(
                    player = player,
                    position = LatLng(
                        this.player.position.latitude - randomLatitude,
                        this.player.position.longitude - randomLongitude
                    ), // Values in 0.0001 or lower because of scale
                    mMap = mMap
                )
            }
            else if(dice <8)
            {
                this.monster = MonsterWandering(
                    player = player,
                    position = LatLng(
                        this.player.position.latitude - randomLatitude,
                        this.player.position.longitude - randomLongitude
                    ), // Values in 0.0001 or lower because of scale
                    mMap = mMap
                )
            }
            else if(dice<11)
            {
                this.monster = MonsterMoving(
                    player = player,
                    position = LatLng(
                        this.player.position.latitude - randomLatitude,
                        this.player.position.longitude - randomLongitude
                    ), // Values in 0.0001 or lower because of scale
                    mMap = mMap
                )
            }
        }
        else if(selectedDifficulty == "Hard")
        {
            if(dice < 4) {
                this.monster = MonsterStanding(
                    player = player,
                    position = LatLng(
                        this.player.position.latitude - randomLatitude,
                        this.player.position.longitude - randomLongitude
                    ), // Values in 0.0001 or lower because of scale
                    mMap = mMap
                )
            }
            else if(dice <7)
            {
                this.monster = MonsterWandering(
                    player = player,
                    position = LatLng(
                        this.player.position.latitude - randomLatitude,
                        this.player.position.longitude - randomLongitude
                    ), // Values in 0.0001 or lower because of scale
                    mMap = mMap
                )
            }
            else if(dice<11)
            {
                this.monster = MonsterMoving(
                    player = player,
                    position = LatLng(
                        this.player.position.latitude - randomLatitude,
                        this.player.position.longitude - randomLongitude
                    ), // Values in 0.0001 or lower because of scale
                    mMap = mMap
                )
            }
        }
        this.monsterController.monsterActivate(this.monster)
    }
    private fun randomPosition(): Double
    {
        var randomNumber = 0
        while (randomNumber == 0) {
            randomNumber = Random.nextInt(-9, 10)
        }
        return randomNumber.toDouble()/10000
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

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                currentPlayerLocation = currentLatLng
                mMap.addMarker(MarkerOptions().position(currentLatLng).title(MARKER_TITLE))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))

                if (!this.initialized) this.gameInit(currentLatLng)

                this.player.changePosition(currentLatLng)


                // Make spawner with monster list invoke this action
                // this.monsterController.monsterActivate(monster)

                // Then this next action has to be ran every time the player changes position
                // this.monsterController.monsterAlert(monster)
                // Best to make Spawner.update method that calls this here <<
            } else {
                Toast.makeText(this, TOAST_TEXT_ERROR, Toast.LENGTH_SHORT).show()
            }
        }
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
}
