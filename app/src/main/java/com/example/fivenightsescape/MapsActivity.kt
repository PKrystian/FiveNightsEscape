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
import com.example.fivenightsescape.monster.MonsterController
import com.example.fivenightsescape.player.Player
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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

    fun gameInit(currentLatLng: LatLng)
    {
        this.player = Player(position = currentLatLng)

        // Make initial mobs spawn here
        // After creating class for spawning mobs use as following:
//        var monster = MonsterClass(
//            player = player,
//            position = LatLng(monsterPosition), // Values in 0.0001 or lower because of scale
//            mMap = mMap
//        )

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

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
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
