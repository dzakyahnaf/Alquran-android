package com.azhar.alquran.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azhar.alquran.model.main.ModelAyat
import com.azhar.alquran.model.main.ModelResult
import com.azhar.alquran.model.main.ModelSurah
import com.azhar.alquran.model.main.ModelSuratDetail
import com.azhar.alquran.networking.ApiInterface
import com.azhar.alquran.networking.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by Azhar Rivaldi on 11-11-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * LinkedIn : https://www.linkedin.com/in/azhar-rivaldi
 */

class SurahViewModel : ViewModel() {
    private val modelSurahMutableLiveData = MutableLiveData<ArrayList<ModelSurah>>()
    private val modelAyatMutableLiveData = MutableLiveData<ArrayList<ModelAyat>>()

    fun setSurah() {
        val apiService: ApiInterface = ApiService.getQuran()
        val call = apiService.getListSurah()

        call.enqueue(object : Callback<ModelResult<ArrayList<ModelSurah>>> {
            override fun onResponse(call: Call<ModelResult<ArrayList<ModelSurah>>>, response: Response<ModelResult<ArrayList<ModelSurah>>>) {
                if (!response.isSuccessful) {
                    Log.e("SurahViewModel", "Response Error: ${response.code()} ${response.message()}")
                    modelSurahMutableLiveData.postValue(ArrayList())
                } else {
                    val body = response.body()
                    if (body != null) {
                        Log.d("SurahViewModel", "API Response: code=${body.code}, message=${body.message}")
                        if (body.code == 200 || body.code.toString() == "200") {
                            val items: ArrayList<ModelSurah> = body.data ?: ArrayList()
                            Log.d("SurahViewModel", "Loaded ${items.size} surahs")
                            modelSurahMutableLiveData.postValue(items)
                        } else {
                            Log.e("SurahViewModel", "API Business Error: ${body.message}")
                            modelSurahMutableLiveData.postValue(ArrayList())
                        }
                    } else {
                        Log.e("SurahViewModel", "Response body is null")
                        modelSurahMutableLiveData.postValue(ArrayList())
                    }
                }
            }

            override fun onFailure(call: Call<ModelResult<ArrayList<ModelSurah>>>, t: Throwable) {
                Log.e("SurahViewModel", "Network Failure: ${t.message}", t)
                modelSurahMutableLiveData.postValue(ArrayList())
            }
        })
    }

    fun setDetailSurah(nomor: String) {
        val apiService: ApiInterface = ApiService.getQuran()
        val call = apiService.getDetailSurah(nomor)

        call.enqueue(object : Callback<ModelResult<ModelSuratDetail>> {
            override fun onResponse(call: Call<ModelResult<ModelSuratDetail>>, response: Response<ModelResult<ModelSuratDetail>>) {
                if (!response.isSuccessful) {
                    Log.e("SurahViewModel", "Response Error: ${response.code()} ${response.message()}")
                    modelAyatMutableLiveData.postValue(ArrayList())
                } else {
                    val body = response.body()
                    if (body != null) {
                        Log.d("SurahViewModel", "API Response: code=${body.code}, message=${body.message}")
                        if (body.code == 200 || body.code.toString() == "200") {
                            val items: ArrayList<ModelAyat> = body.data?.verses ?: ArrayList()
                            Log.d("SurahViewModel", "Loaded ${items.size} verses")
                            modelAyatMutableLiveData.postValue(items)
                        } else {
                            Log.e("SurahViewModel", "API Business Error: ${body.message}")
                            modelAyatMutableLiveData.postValue(ArrayList())
                        }
                    } else {
                        Log.e("SurahViewModel", "Response body is null")
                        modelAyatMutableLiveData.postValue(ArrayList())
                    }
                }
            }

            override fun onFailure(call: Call<ModelResult<ModelSuratDetail>>, t: Throwable) {
                Log.e("SurahViewModel", "Network Failure: ${t.message}", t)
                modelAyatMutableLiveData.postValue(ArrayList())
            }
        })
    }

    fun getSurah(): LiveData<ArrayList<ModelSurah>> = modelSurahMutableLiveData

    fun getDetailSurah(): LiveData<ArrayList<ModelAyat>> = modelAyatMutableLiveData
}