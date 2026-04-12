package com.azhar.alquran.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.azhar.alquran.R
import com.azhar.alquran.activities.DetailActivity
import com.azhar.alquran.databinding.ListItemSurahBinding
import com.azhar.alquran.model.main.ModelSurah
import java.util.*

/**
 * Created by Azhar Rivaldi on 10-11-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * LinkedIn : https://www.linkedin.com/in/azhar-rivaldi
 */

class MainAdapter(private val mContext: Context) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    private val modelSurahList = ArrayList<ModelSurah>()

    fun setAdapter(items: ArrayList<ModelSurah>) {
        modelSurahList.clear()
        modelSurahList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = modelSurahList[position]

        holder.binding.tvNumber.text = data.nomor.toString()
        holder.binding.tvName.text = data.asma
        holder.binding.tvAyat.text = data.nama
        holder.binding.tvInfo.text = "${data.type} - ${data.ayat} Ayat"

        holder.binding.cvSurah.setOnClickListener {
            val intent = Intent(mContext, DetailActivity::class.java)
            intent.putExtra(DetailActivity.DETAIL_SURAH, modelSurahList[position])
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return modelSurahList.size
    }

    //Class Holder
    class ViewHolder(val binding: ListItemSurahBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}