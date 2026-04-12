package com.azhar.alquran.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azhar.alquran.R
import com.azhar.alquran.databinding.ListItemAyatBinding
import com.azhar.alquran.model.main.ModelAyat
import java.util.*

/**
 * Created by Azhar Rivaldi on 10-11-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * LinkedIn : https://www.linkedin.com/in/azhar-rivaldi
 */

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    private val modelAyatList = ArrayList<ModelAyat>()

    fun setAdapter(items: ArrayList<ModelAyat>) {
        modelAyatList.clear()
        modelAyatList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemAyatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = modelAyatList[position]

        holder.binding.tvNomorAyat.text = data.nomor.toString()
        holder.binding.tvArabic.text = data.arab
        holder.binding.tvTerjemahan.text = data.indo
    }

    override fun getItemCount(): Int {
        return modelAyatList.size
    }

    //Class Holder
    class ViewHolder(val binding: ListItemAyatBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}