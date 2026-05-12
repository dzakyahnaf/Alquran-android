package com.azhar.alquran.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.azhar.alquran.R
import com.azhar.alquran.databinding.ActivityMasjidBinding
import com.azhar.alquran.model.nearby.ModelResults
import com.azhar.alquran.viewmodel.MasjidViewModel
import im.delight.android.location.SimpleLocation
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import java.util.*

class MasjidActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasjidBinding

    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0
    lateinit var strCurrentLocation: String
    lateinit var simpleLocation: SimpleLocation
    lateinit var progressDialog: android.app.ProgressDialog
    lateinit var masjidViewModel: MasjidViewModel
    var REQ_PERMISSION = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // OSMDroid configuration
        Configuration.getInstance().userAgentValue = packageName
        
        binding = ActivityMasjidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = android.app.ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu…")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("sedang menampilkan lokasi")

        setPermission()
        setInitLayout()
        
        // Initial map setup
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
    }

    private fun setInitLayout() {
        binding.toolbar.setTitle(null)
        setSupportActionBar(binding.toolbar)
        assert(supportActionBar != null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        simpleLocation = SimpleLocation(this, false)
        
        // Start updates immediately
        simpleLocation.beginUpdates()

        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        simpleLocation.setListener(object : SimpleLocation.Listener {
            override fun onPositionChanged() {
                val lat = simpleLocation.latitude
                val lng = simpleLocation.longitude
                // Jika lokasi sudah didapatkan dan sebelumnya 0.0 (belum fetch)
                if (lat != 0.0 && lng != 0.0 && strCurrentLatitude == 0.0) {
                    strCurrentLatitude = lat
                    strCurrentLongitude = lng
                    strCurrentLocation = "$strCurrentLatitude,$strCurrentLongitude"
                    setViewModel()
                }
            }
        })

        //get location initially if available
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude

        if (strCurrentLatitude != 0.0 || strCurrentLongitude != 0.0) {
            // Sudah ada lokasi dari cache
            strCurrentLocation = "$strCurrentLatitude,$strCurrentLongitude"
            setViewModel()
        } else {
            // Menunggu listener mendapatkan lokasi
            progressDialog.setMessage("Sedang mencari titik lokasi GPS Anda...")
            progressDialog.show()
            
            // Timeout 10 detik agar tidak stuck
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                if (strCurrentLatitude == 0.0 && progressDialog.isShowing) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Gagal mendapatkan lokasi GPS. Pastikan GPS Anda aktif.", Toast.LENGTH_SHORT).show()
                }
            }, 10000)
        }
    }

    private fun setPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQ_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }

    private fun setViewModel() {
        progressDialog.setMessage("Sedang mencari masjid terdekat...")
        if (!progressDialog.isShowing) progressDialog.show()
        
        masjidViewModel = ViewModelProvider(this, NewInstanceFactory()).get(MasjidViewModel::class.java)
        masjidViewModel.setMarkerLocation(strCurrentLocation)
        masjidViewModel.getMarkerLocation()
            .observe(this, { modelResults: ArrayList<ModelResults> ->
                if (modelResults.size != 0) {
                    getMarker(modelResults)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(this, "Oops, tidak ada masjid yang ditemukan di sekitar lokasi Anda!", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
                progressDialog.dismiss()
            })
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
        simpleLocation.beginUpdates()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        simpleLocation.endUpdates()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}