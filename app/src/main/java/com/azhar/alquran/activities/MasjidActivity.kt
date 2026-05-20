package com.azhar.alquran.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.azhar.alquran.databinding.ActivityMasjidBinding
import com.azhar.alquran.model.nearby.ModelResults
import com.azhar.alquran.viewmodel.MasjidViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import java.util.*

class MasjidActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MasjidActivity"
        private const val REQ_PERMISSION = 1000
        private const val LOCATION_TIMEOUT_MS = 15000L
    }

    private lateinit var binding: ActivityMasjidBinding
    private lateinit var progressDialog: android.app.ProgressDialog
    private lateinit var masjidViewModel: MasjidViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var strCurrentLatitude = 0.0
    private var strCurrentLongitude = 0.0
    private var strCurrentLocation = ""
    private var locationFound = false

    private var locationCallback: LocationCallback? = null
    private val timeoutHandler = android.os.Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSMDroid configuration
        Configuration.getInstance().userAgentValue = packageName

        binding = ActivityMasjidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        binding.toolbar.setTitle(null)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup progress dialog
        progressDialog = android.app.ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu…")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Sedang mencari titik lokasi GPS Anda...")

        // Initial map setup
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check permission then start location detection
        checkAndRequestPermission()
    }

    /**
     * Cek permission lokasi. Jika sudah granted, langsung mulai deteksi lokasi.
     * Jika belum, request permission dulu.
     */
    private fun checkAndRequestPermission() {
        if (hasLocationPermission()) {
            startLocationDetection()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQ_PERMISSION
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, mulai deteksi lokasi
                startLocationDetection()
            } else {
                Toast.makeText(
                    this,
                    "Izin lokasi diperlukan untuk mencari masjid terdekat.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Mulai proses deteksi lokasi menggunakan FusedLocationProviderClient.
     * Flow: getLastLocation() → jika null → requestLocationUpdates()
     */
    private fun startLocationDetection() {
        if (!hasLocationPermission()) return

        // Cek apakah GPS/Location services aktif
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            Toast.makeText(this, "Mohon aktifkan GPS/Lokasi di pengaturan perangkat Anda.", Toast.LENGTH_LONG).show()
            return
        }

        progressDialog.show()

        try {
            // Step 1: Coba getLastLocation() dulu (instant, dari cache)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && !locationFound) {
                        Log.d(TAG, "Got location from lastLocation: ${location.latitude}, ${location.longitude}")
                        onLocationObtained(location.latitude, location.longitude)
                    } else {
                        // lastLocation null → request fresh location
                        Log.d(TAG, "lastLocation is null, requesting fresh location...")
                        requestFreshLocation()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "lastLocation failed: ${e.message}")
                    requestFreshLocation()
                }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: ${e.message}")
            dismissProgressSafe()
            Toast.makeText(this, "Izin lokasi tidak tersedia.", Toast.LENGTH_SHORT).show()
        }

        // Timeout safety net
        timeoutHandler.postDelayed({
            if (!locationFound && !isFinishing) {
                stopLocationUpdates()
                dismissProgressSafe()
                Toast.makeText(
                    this,
                    "Gagal mendapatkan lokasi GPS. Pastikan GPS Anda aktif dan coba lagi.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }, LOCATION_TIMEOUT_MS)
    }

    /**
     * Request lokasi baru jika getLastLocation() gagal / null.
     */
    private fun requestFreshLocation() {
        if (!hasLocationPermission() || locationFound) return

        try {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 1000L
                fastestInterval = 500L
                numUpdates = 1
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    if (location != null && !locationFound) {
                        Log.d(TAG, "Got fresh location: ${location.latitude}, ${location.longitude}")
                        onLocationObtained(location.latitude, location.longitude)
                        stopLocationUpdates()
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException on requestLocationUpdates: ${e.message}")
            dismissProgressSafe()
        }
    }

    /**
     * Dipanggil saat lokasi berhasil didapatkan.
     */
    private fun onLocationObtained(latitude: Double, longitude: Double) {
        if (locationFound) return // Prevent duplicate calls
        locationFound = true

        strCurrentLatitude = latitude
        strCurrentLongitude = longitude
        strCurrentLocation = "$strCurrentLatitude,$strCurrentLongitude"

        // Cancel timeout
        timeoutHandler.removeCallbacksAndMessages(null)

        setViewModel()
    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    private fun dismissProgressSafe() {
        if (progressDialog.isShowing && !isFinishing) {
            progressDialog.dismiss()
        }
    }

    private fun setViewModel() {
        progressDialog.setMessage("Sedang mencari masjid terdekat...")
        if (!progressDialog.isShowing && !isFinishing) progressDialog.show()

        masjidViewModel = ViewModelProvider(this, NewInstanceFactory()).get(MasjidViewModel::class.java)
        masjidViewModel.setMarkerLocation(strCurrentLocation)
        masjidViewModel.getMarkerLocation()
            .observe(this) { modelResults: ArrayList<ModelResults> ->
                dismissProgressSafe()
                if (modelResults.size != 0) {
                    getMarker(modelResults)
                } else {
                    Toast.makeText(
                        this,
                        "Oops, tidak ada masjid yang ditemukan di sekitar lokasi Anda!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun getMarker(modelResultsArrayList: ArrayList<ModelResults>) {
        binding.mapView.overlays.clear()

        for (i in modelResultsArrayList.indices) {
            val element = modelResultsArrayList[i]
            val lat = element.modelGeometry.modelLocation.lat
            val lng = element.modelGeometry.modelLocation.lng
            val geoPoint = GeoPoint(lat, lng)

            val marker = Marker(binding.mapView)
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = element.name
            binding.mapView.overlays.add(marker)

            // Center camera to first result
            if (i == 0) {
                val mapController = binding.mapView.controller
                mapController.setZoom(15.0)
                mapController.setCenter(geoPoint)
            }
        }
        binding.mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        timeoutHandler.removeCallbacksAndMessages(null)
        dismissProgressSafe()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}