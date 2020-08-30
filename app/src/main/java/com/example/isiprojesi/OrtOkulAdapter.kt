package com.example.isiprojesi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_okul_ort.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrtOkulAdapter(val myContext: Context, val okulOrtData: ArrayList<OkulOrtData>) : RecyclerView.Adapter<OrtOkulAdapter.OrtOkulAdapterHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrtOkulAdapter.OrtOkulAdapterHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_okul_ort, parent, false)
        return OrtOkulAdapter.OrtOkulAdapterHolder(view)
    }

    override fun getItemCount(): Int {
        return okulOrtData.size
    }

    override fun onBindViewHolder(holder: OrtOkulAdapterHolder, position: Int) {
        var item = okulOrtData[position]

        holder.setDataa(myContext, item)
    }

    class OrtOkulAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tarihGun = itemView.tvTarihGun
        var tarihAy = itemView.tvTarihAy

        var ortOkulSicakligi = itemView.tvOrtOkulSicaklik


        fun setDataa(myContext: Context, item: OkulOrtData) {
            tarihGun.text = formatDate(item.olcum_zamani, "Gun").toString()
            tarihAy.text = formatDate(item.olcum_zamani, "Ay").toString()

           ortOkulSicakligi.text = item.ort_sicaklik!!.toDouble().toString()
        }

        fun formatDate(miliSecond: Long?, zaman: String): String? {
            if (zaman == "Gun") {
                if (miliSecond == null) return "0"
                val date = Date(miliSecond)
                val sdf = SimpleDateFormat("d", Locale("tr"))
                return sdf.format(date)
            } else if (zaman == "Ay") {
                if (miliSecond == null) return "0"
                val date = Date(miliSecond)
                val sdf = SimpleDateFormat("MMM", Locale("tr"))
                return sdf.format(date)
            }else{
                return "0"
            }

        }


    }


}