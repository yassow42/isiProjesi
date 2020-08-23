package com.example.isiprojesi

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.item_ogrenci.view.*
import kotlinx.android.synthetic.main.item_ogrenci.view.tvNumara
import kotlinx.android.synthetic.main.item_ogrenci.view.tvSonSicaklik

class MainAdapter(val myContext: Context, val numaraList: ArrayList<Int>) : RecyclerView.Adapter<MainAdapter.MainHolder>() {
    var lineDataSet = LineDataSet(null, null)
    var lineDataSets = ArrayList<LineDataSet>()
    lateinit var lineData: LineData


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_ogrenci, parent, false)

        return MainHolder(view)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        var numara = numaraList[position].toString()
        holder.tvNumara.text = numara
        holder.setData(myContext, numara)
        val animation = AnimationUtils.loadAnimation(myContext, R.anim.item_animation_fall_down)
        holder.itemView.startAnimation(animation)
        holder.itemView.setOnClickListener {

            var bottomSheetDialog = Dialog(myContext, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
            //   var viewBottom = bottomSheetDialog.layoutInflater.inflate(R.layout.bottom_sheet_dialog,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            // bottomSheetDialog.setContentView(viewBottom)
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)

            var ref = FirebaseDatabase.getInstance().reference
            ref.child("isi_verileri").child(numara).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    bottomSheetDialog.tvNumara.text = numara
                    bottomSheetDialog.tvSonSicaklik.text = p0.child("guncel_sicakliklar").value.toString()

                    val entries = ArrayList<Entry>()
                    val logSicakliklar = ArrayList<Float>()
                    val labels = ArrayList<String>()
                    for (i in p0.child("logs_sicakliklar").children) {
                        logSicakliklar.add(i.value.toString().toFloat())
                    }
                    for (sayi in 0..logSicakliklar.size - 1) {
                        entries.add(Entry(sayi.toFloat(), logSicakliklar[sayi].toFloat()))
                        labels.add((sayi).toString() + " ölçüm")
                    }
                    showChart(entries, bottomSheetDialog.lineChartViewBottom, logSicakliklar, position)

                }

                override fun onCancelled(error: DatabaseError) {


                }

            })
            bottomSheetDialog.show()
        }


    }

    private fun showChart(entries: ArrayList<Entry>, gnlineChartView: LineChart, logSicakliklar: ArrayList<Float>, position: Int) {
        lineDataSet.setValues(entries)
        lineDataSet.setLabel("Sıcaklık Değeri")
        lineDataSets.clear()
        lineDataSets.add(lineDataSet)
        lineData = LineData(lineDataSets as List<ILineDataSet>?)


        lineDataSet.setColor(ContextCompat.getColor(myContext, R.color.mavi))
        lineDataSet.lineWidth = 2f
        lineDataSet.setCircleColor(ContextCompat.getColor(myContext, R.color.yesil))
        lineDataSet.valueTextColor = R.color.beyaz
        lineDataSet.valueTextSize = 10f

        gnlineChartView.clear()
        gnlineChartView.data = lineData
        gnlineChartView.invalidate()


        gnlineChartView.setNoDataText("Veri Alınamadı")
        gnlineChartView.setNoDataTextColor(R.color.kirmizi)
        gnlineChartView.setDrawGridBackground(true)
        gnlineChartView.setGridBackgroundColor(ContextCompat.getColor(myContext, R.color.sari))
        gnlineChartView.setBackgroundColor(ContextCompat.getColor(myContext, R.color.beyaz))
        gnlineChartView.setDrawBorders(true)
        gnlineChartView.setBorderWidth(0.3f)

        lineDataSet.valueTextSize = 12f
        lineDataSet.setDrawValues(true)
        lineDataSet.setDrawCircles(true)
        lineDataSet.setHighlightEnabled(true)


        lineDataSet.valueTextColor = Color.BLUE
        lineDataSet.setCircleColor(ContextCompat.getColor(myContext, R.color.kirmizi))
        lineDataSet.lineWidth = 3f
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.enableDashedLine(10f, 4f, 0f)


        gnlineChartView.xAxis.textSize = 12f
        gnlineChartView.description.isEnabled = false
        gnlineChartView.axisLeft.isEnabled = false
        gnlineChartView.xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        gnlineChartView.xAxis.labelCount = 8
        gnlineChartView.xAxis.xOffset = 3f
        gnlineChartView.axisLeft.yOffset = 8f


    }

    override fun getItemCount(): Int {
        return numaraList.size
    }


    class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvNumara = itemView.tvNumara
        val tvSonSicaklik = itemView.tvSonSicaklik
        val lineChartView = itemView.lineChartView


        val ref = FirebaseDatabase.getInstance().reference
        var lineDataSet = LineDataSet(null, null)
        var lineDataSets = ArrayList<LineDataSet>()
        lateinit var lineData: LineData


        fun setData(myContext: Context, numara: String) {

            ref.child("isi_verileri").child(numara).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        tvSonSicaklik.text = p0.child("guncel_sicakliklar").value.toString()
                        if (p0.child("guncel_sicakliklar").value.toString() == "-127") {
                            tvSonSicaklik.text = "Veri Yanlış"
                        }
                        val entries = ArrayList<Entry>()
                        val logSicakliklar = ArrayList<Float>()
                        val labels = ArrayList<String>()

                        for (i in p0.child("logs_sicakliklar").children) {
                            logSicakliklar.add(i.value.toString().toFloat())
                            if (i.value.toString() == "-127") {
                                ref.child("isi_verileri").child(numara).child("logs_sicakliklar").child(i.key.toString()).removeValue()
                            }
                        }

                        if (logSicakliklar.size < 10) {
                            for (sayi in 0..logSicakliklar.size - 1) {
                                entries.add(Entry(sayi.toFloat(), logSicakliklar[sayi].toFloat()))
                                labels.add((sayi).toString())
                            }
                        } else {
                            for (sayi in logSicakliklar.size - 7..logSicakliklar.size - 1) {
                                entries.add(Entry(sayi.toFloat(), logSicakliklar[sayi].toFloat()))
                                labels.add((sayi).toString())
                            }
                        }

                        showChart(entries, lineChartView, myContext)
                    } else {
                        lineChartView.clear()
                        lineChartView.invalidate()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })


        }

        private fun showChart(entries: ArrayList<Entry>, gnlineChartView: LineChart, myContext: Context) {
            lineDataSet.setValues(entries)
            lineDataSet.setLabel("Vücut Isısı")
            lineDataSets.clear()
            lineDataSets.add(lineDataSet)
            lineData = LineData(lineDataSets as List<ILineDataSet>?)

            lineDataSet.setColors(ContextCompat.getColor(myContext, R.color.mavi))
            lineDataSet.lineWidth = 2f
            lineDataSet.setCircleColor(ContextCompat.getColor(myContext, R.color.yesil))
            lineDataSet.valueTextColor = R.color.beyaz
            lineDataSet.valueTextSize = 10f

            gnlineChartView.setDragOffsetX(15f)

            gnlineChartView.setNoDataText("Veri Alınamadı")
            gnlineChartView.setNoDataTextColor(R.color.kirmizi)
            gnlineChartView.setDrawGridBackground(true)
            gnlineChartView.setGridBackgroundColor(ContextCompat.getColor(myContext, R.color.sari))
            gnlineChartView.setBackgroundColor(ContextCompat.getColor(myContext, R.color.beyaz))
            gnlineChartView.setDrawBorders(true)
            gnlineChartView.setBorderWidth(0.3f)



            gnlineChartView.clear()
            gnlineChartView.data = lineData
            gnlineChartView.invalidate()
            gnlineChartView.animateY(1000)

            gnlineChartView.description.isEnabled = false
            gnlineChartView.axisLeft.isEnabled = false
            gnlineChartView.axisLeft.textSize = 8f
            gnlineChartView.xAxis.textSize = 8f

            gnlineChartView.xAxis.labelCount = entries.size
            //   gnlineChartView.extraBottomOffset = 8f
            gnlineChartView.xAxis.setDrawAxisLine(false)
            gnlineChartView.xAxis.setCenterAxisLabels(true)

            gnlineChartView.xAxis.isEnabled = false
            gnlineChartView.axisLeft.setEnabled(false)
            gnlineChartView.axisRight.setEnabled(false)


        }


    }


}

