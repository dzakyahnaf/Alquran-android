package com.azhar.alquran.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azhar.alquran.model.nearby.ModelGeometry
import com.azhar.alquran.model.nearby.ModelLocation
import com.azhar.alquran.model.nearby.ModelResultOSM
import com.azhar.alquran.model.nearby.ModelResults
import com.azhar.alquran.networking.ApiInterface
import com.azhar.alquran.networking.ApiOSM
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by Azhar Rivaldi on 10-11-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * LinkedIn : https://www.linkedin.com/in/azhar-rivaldi
 */

class MasjidViewModel : ViewModel() {

    private val modelResultsMutableLiveData = MutableLiveData<ArrayList<ModelResults>>()

    fun setMarkerLocation(strLocation: String) {
        // strLocation format is "lat,lng" from MainActivity
        val apiService: ApiInterface = ApiOSM.getOSM()

        // Overpass QL query: find mosques within 5000m of the given coordinates
        // Usage: [out:json];node(around:radius,lat,lon)[tags];out;
        val query = "[out:json];node(around:5000,$strLocation)[\"amenity\"=\"place_of_worship\"][\"religion\"=\"muslim\"];out;"

        val call = apiService.getMasjidOSM(query)
        call.enqueue(object : Callback<ModelResultOSM> {
            override fun onResponse(call: Call<ModelResultOSM>, response: Response<ModelResultOSM>) {
                if (!response.isSuccessful) {
                    Log.e("response", response.toString())
                    modelResultsMutableLiveData.postValue(ArrayList())
                } else if (response.body() != null) {
                    val osmElements = response.body()?.elements ?: ArrayList()
                    val items = ArrayList<ModelResults>()

                    // Transform OSM elements to existing ModelResults structure
                    for (element in osmElements) {
                        val modelResults = ModelResults()
                        modelResults.name = element.tags?.name ?: "Masjid"
                        
                        val geometry = ModelGeometry()
                        val location = ModelLocation()
                        location.lat = element.lat
                        location.lng = element.lon
                        geometry.modelLocation = location
                        modelResults.modelGeometry = geometry
                        
                        items.add(modelResults)
                    }
                    modelResultsMutableLiveData.postValue(items)
                } else {
                    modelResultsMutableLiveData.postValue(ArrayList())
                }
            }

            override fun onFailure(call: Call<ModelResultOSM>, t: Throwable) {
                Log.e("failure", t.toString())
                modelResultsMutableLiveData.postValue(ArrayList())
            }
        })
    }

    fun getMarkerLocation(): LiveData<ArrayList<ModelResults>> = modelResultsMutableLiveData

}