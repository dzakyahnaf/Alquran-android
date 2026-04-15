package com.azhar.alquran.fragment

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import com.azhar.alquran.R
import com.azhar.quran.model.DaftarKota
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vivekkaushik.datepicker.DatePickerTimeline
import com.azhar.alquran.databinding.FragmentJadwalSholatBinding
import java.util.*
import com.azhar.alquran.model.main.ModelPrayerResult
import com.azhar.alquran.model.main.ModelResult
import com.azhar.alquran.model.main.ShalatRequest
import com.azhar.alquran.networking.ApiInterface
import com.azhar.alquran.networking.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class FragmentJadwalSholat : BottomSheetDialogFragment() {

    private var _binding: FragmentJadwalSholatBinding? = null
    private val binding get() = _binding!!

    lateinit var strArg: String
    lateinit var listDaftarKota: MutableList<DaftarKota>
    lateinit var daftarKotaAdapter: ArrayAdapter<DaftarKota>
    lateinit var progressDialog: ProgressDialog

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    companion object {
        @JvmStatic
        fun newInstance(string: String?): FragmentJadwalSholat {
            val fragment = FragmentJadwalSholat()
            val args = Bundle()
            args.putString("Jadwal Sholat", string)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        strArg = requireArguments().getString("Jadwal Sholat").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJadwalSholatBinding.inflate(inflater, container, false)
        val rootView = binding.root
        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Sedang menampilkan jadwal...")

        //show data spinner
        val spKota: Spinner = binding.spinKota
        listDaftarKota = ArrayList()
        daftarKotaAdapter = ArrayAdapter(
            requireActivity().getApplicationContext(),
            android.R.layout.simple_spinner_item,
            listDaftarKota as ArrayList<DaftarKota>
        )
        daftarKotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spKota.adapter = daftarKotaAdapter
        spKota.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>) {}
            override fun onItemSelected(p0: AdapterView<*>, view: View?, position: Int, id: Long) {
                val spinKota = daftarKotaAdapter.getItem(position)
                if (spinKota != null) {
                    getDataJadwal(spinKota.provinsi, spinKota.kabkota)
                }
            }
        }

        //show date time
        val datePickerTimeline: DatePickerTimeline = binding.dateTimeline
        val date = Calendar.getInstance()
        val mYear: Int = date.get(Calendar.YEAR)
        val mMonth: Int = date.get(Calendar.MONTH)
        val mDay: Int = date.get(Calendar.DAY_OF_MONTH)

        datePickerTimeline.setInitialDate(mYear, mMonth, mDay)
        datePickerTimeline.setDisabledDateColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorAccent
            )
        )
        datePickerTimeline.setActiveDate(date)

        val dates = arrayOf(Calendar.getInstance().time)
        datePickerTimeline.deactivateDates(dates)

        //get data kota
        getDataKota()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDataJadwal(provinsi: String?, kabkota: String?) {
        val prov = provinsi ?: "DKI Jakarta"
        val kota = kabkota ?: "Kota Jakarta"
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val request = ShalatRequest(
            provinsi = prov,
            kabkota = kota,
            bulan = month,
            tahun = year
        )

        progressDialog.show()
        val apiService: ApiInterface = ApiService.getQuran()
        val call = apiService.getJadwalSholat(request)

        call.enqueue(object : Callback<ModelResult<ModelPrayerResult>> {
            override fun onResponse(call: Call<ModelResult<ModelPrayerResult>>, response: Response<ModelResult<ModelPrayerResult>>) {
                progressDialog.dismiss()
                if (response.isSuccessful && response.body() != null && response.body()!!.code == 200) {
                    val listJadwal = response.body()!!.data?.jadwal
                    if (listJadwal != null && listJadwal.size >= day) {
                        val data = listJadwal[day - 1]
                        binding.tvSubuh.text = data.subuh ?: "-"
                        binding.tvDzuhur.text = data.dzuhur ?: "-"
                        binding.tvAshar.text = data.ashar ?: "-"
                        binding.tvMaghrib.text = data.maghrib ?: "-"
                        binding.tvIsya.text = data.isya ?: "-"
                        Log.d("FragmentJadwalSholat", "Jadwal loaded: Subuh=${data.subuh}, Dzuhur=${data.dzuhur}, Ashar=${data.ashar}, Maghrib=${data.maghrib}, Isya=${data.isya}")
                    } else {
                        Log.e("FragmentJadwalSholat", "No schedule data for day $day. List size: ${listJadwal?.size}")
                    }
                } else {
                    Log.e("FragmentJadwalSholat", "API Error: code=${response.code()}, body=${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ModelResult<ModelPrayerResult>>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("FragmentJadwalSholat", "Network error: ${t.message}", t)
            }
        })
    }

    private fun getDataKota() {
        listDaftarKota.clear()
        val cityData = arrayOf(
            Pair("DKI Jakarta", "Kota Jakarta"),
            Pair("Jawa Barat", "Kota Bandung"),
            Pair("Jawa Barat", "Kota Bogor"),
            Pair("Jawa Barat", "Kota Bekasi"),
            Pair("Jawa Barat", "Kota Depok"),
            Pair("Banten", "Kota Tangerang"),
            Pair("Jawa Tengah", "Kota Semarang"),
            Pair("D.I. Yogyakarta", "Kota Yogyakarta"),
            Pair("Jawa Timur", "Kota Surabaya"),
            Pair("Jawa Timur", "Kota Malang"),
            Pair("Sumatera Utara", "Kota Medan"),
            Pair("Sumatera Barat", "Kota Padang"),
            Pair("Riau", "Kota Pekanbaru"),
            Pair("Sulawesi Selatan", "Kota Makassar"),
            Pair("Bali", "Kota Denpasar")
        )
        for (city in cityData) {
            val d = DaftarKota()
            d.provinsi = city.first
            d.kabkota = city.second
            listDaftarKota.add(d)
        }
        daftarKotaAdapter.notifyDataSetChanged()
    }


}