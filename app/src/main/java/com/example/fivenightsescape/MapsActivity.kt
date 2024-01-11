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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    //timer and lv things
    private var levelChanger = 4 //levelChanger * delay = every how many milliseconds next lv, it changes every two levels
    private val delay: Long = 500 // how often does the progressbar update
    private var currentLevel = 1 //lv the player is on
    private var subLevel = 0 // for the progressbar and to change level length
    private val handlerTimer = Handler()
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
                    subLevel+=1
                    progressBar.progress = (subLevel.toDouble() / levelChanger * 100).toInt()
                }
                else{
                    if (currentLevel%2==0){
                        levelChanger*=2 // every two levels it takes longer to get another lv
                    }
                    progressBar.progress = 0
                    subLevel = 0
                    currentLevel +=1
                    levelText.text = "Level: " + currentLevel.toString()
                }
            }
            handlerTimer.postDelayed(this, delay)
        }
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
