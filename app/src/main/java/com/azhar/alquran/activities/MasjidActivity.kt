package com.azhar.alquran.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var progressDialog: ProgressDialog
    lateinit var masjidViewModel: MasjidViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // OSMDroid configuration
        Configuration.getInstance().userAgentValue = packageName
        
        binding = ActivityMasjidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu…")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("sedang menampilkan lokasi")

        setInitLayout()
        
        // Initial map setup
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        
        setViewModel()
    }

    private fun setInitLayout() {
        binding.toolbar.setTitle(null)
        setSupportActionBar(binding.toolbar)
        assert(supportActionBar != null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        simpleLocation = SimpleLocation(this)
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        //get location
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude

        //set location lat long
        strCurrentLocation = "$strCurrentLatitude,$strCurrentLongitude"
    }

    private fun setViewModel() {
        progressDialog.show()
        masjidViewModel = ViewModelProvider(this, NewInstanceFactory()).get(MasjidViewModel::class.java)
        masjidViewModel.setMarkerLocation(strCurrentLocation)
        masjidViewModel.getMarkerLocation()
            .observe(this, { modelResults: ArrayList<ModelResults> ->
                if (modelResults.size != 0) {
                    getMarker(modelResults)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(this, "Oops, tidak bisa mendapatkan lokasi kamu!", Toast.LENGTH_SHORT).show()
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
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}