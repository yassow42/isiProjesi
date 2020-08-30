package com.example.isiprojesi

import android.app.Fragment.instantiate
import android.app.PendingIntent.getActivity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_ogrenci.view.*
import kotlin.random.Random

class OgrAdapter(val myContext: Context, val numaraList: ArrayList<Int>, val ortOkulSicakliklari: ArrayList<Float>) : RecyclerView.Adapter<OgrAdapter.MainHolder>() {
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
        holder.setData(myContext, numara, ortOkulSicakliklari)
        val animation = AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme)
        holder.itemView.startAnimation(animation)


/*
        holder.clVeriler.setOnClickListener {



        /*
        var bottomSheetDialog = Dialog(it.context.applicationContext)
        //   var viewBottom = bottomSheetDialog.layoutInflater.inflate(R.layout.bottom_sheet_dialog,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        // bottomSheetDialog.setContentView(viewBottom)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        bottomSheetDialog.setTitle(numara)

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
                Log.e("sad", logSicakliklar.size.toString())
                for (sayi in 0..logSicakliklar.size - 1) {
                    entries.add(Entry(sayi.toFloat(), logSicakliklar[sayi].toFloat()))
                    labels.add((sayi).toString() + " ölçüm")
                }
                holder.tvOrtSicaklik.text = logSicakliklar.average().toString()
                showChart(entries, bottomSheetDialog.lineChartViewBottom, logSicakliklar, position)
                bottomSheetDialog.show()
            }

            override fun onCancelled(error: DatabaseError) {


            }

        })
*/
    }
*/

}

override fun getItemCount(): Int {
    return numaraList.size
}

class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val clVeriler = itemView.clVeriler
    val clNo = itemView.clNo

    val tvNumara = itemView.tvNumara
    val tvSonSicaklik = itemView.tvSonSicaklik
    val tvOrtSicaklik = itemView.tvOrtSicaklik
    val lineChartView = itemView.lineChartView


    val ref = FirebaseDatabase.getInstance().reference
    var lineDataSet1 = LineDataSet(null, null)
    var lineDataSet2 = LineDataSet(null, null)
    var lineDataSets1 = ArrayList<LineDataSet>()

    //  var lineDataSets2 = ArrayList<LineDataSet>()
    lateinit var lineData1: LineData
    lateinit var lineData2: LineData


    fun setData(myContext: Context, numara: String, ortOkulSicakliklari: ArrayList<Float>) {
        val ogrEntries = ArrayList<Entry>()
        val okulEntries = ArrayList<Entry>()
        val ortOgrLogSicakliklar = ArrayList<Float>()
        val ortOkulSicakliklar = ArrayList<Float>()

        ref.child("isi_verileri").child(numara).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    tvSonSicaklik.text = p0.child("guncel_sicakliklar").value.toString()
                    if (p0.child("guncel_sicakliklar").value.toString() == "-127") {
                        tvSonSicaklik.text = "Veri Yanlış"
                    }

                    //   val ogrLabels = ArrayList<String>()

                    for (i in p0.child("logs_sicakliklar").children) {
                        ortOgrLogSicakliklar.add(i.value.toString().toFloat())
                        if (i.value.toString() == "-127") {
                            ref.child("isi_verileri").child(numara).child("logs_sicakliklar").child(i.key.toString()).removeValue()
                        }
                    }


                    if (ortOgrLogSicakliklar.size < ortOkulSicakliklari.size) {

                        if (ortOgrLogSicakliklar.size < 10) {
                            for (sayi in 0..ortOgrLogSicakliklar.size - 1) {
                                ogrEntries.add(Entry(sayi.toFloat(), ortOgrLogSicakliklar[sayi].toFloat()))
                                okulEntries.add(Entry(sayi.toFloat(), ortOkulSicakliklari[sayi].toFloat()))
                                //   ogrLabels.add((sayi).toString())
                            }
                        } else {
                            for (sayi in ortOgrLogSicakliklar.size - 8..ortOgrLogSicakliklar.size - 1) {
                                ogrEntries.add(Entry(sayi.toFloat(), ortOgrLogSicakliklar[sayi].toFloat()))
                                okulEntries.add(Entry(sayi.toFloat(), ortOkulSicakliklari[sayi].toFloat()))
                                //   ogrLabels.add((sayi).toString())
                            }
                        }
                        //  Log.e("dongu", "if içinde")

                    } else {
                        //  Log.e("dongu", "else")


                        var key = ArrayList<String>()
                        for (i in p0.child("logs_sicakliklar").children) {
                            key.add(i.key.toString())
                        }
                        //     Log.e("rastgele key",key[Random.nextInt(0,key.size-1)].toString())
                        ref.child("isi_verileri").child(numara).child("logs_sicakliklar").child(key[Random.nextInt(0, key.size - 1)].toString()).removeValue()

                    }


                    //     Log.e("ortOgrLogSicakliklar", ortOgrLogSicakliklar.size.toString())
                    //    Log.e("ortOkulSicakliklari", ortOkulSicakliklari.size.toString())


                    tvOrtSicaklik.text = ortOgrLogSicakliklar.average().toDouble().toString()
                    ref.child("isi_verileri").child(numara).child("ort_sicaklik").setValue(ortOgrLogSicakliklar.average())
                    showChart(ogrEntries, okulEntries, lineChartView, myContext)
                } else {
                    lineChartView.clear()
                    lineChartView.invalidate()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    private fun showChart(ogrEntries: ArrayList<Entry>, okulEntries: ArrayList<Entry>, gnlineChartView: LineChart, myContext: Context) {
        lineDataSet1.setValues(ogrEntries)
        lineDataSet2.setValues(okulEntries)
        lineDataSet1.setLabel("Ögr. Vücud")
        lineDataSet2.setLabel("Okul Ort.")
        lineDataSets1.clear()

        lineDataSets1.add(lineDataSet1)
        lineDataSets1.add(lineDataSet2)
        lineData1 = LineData(lineDataSets1 as List<ILineDataSet>?)
        //  lineData2 = LineData(lineDataSets2 as List<ILineDataSet>?)

        lineDataSet1.setColors(ContextCompat.getColor(myContext, R.color.mavi))
        lineDataSet2.setColors(ContextCompat.getColor(myContext, R.color.kirmizi))
        lineDataSet1.lineWidth = 2f
        lineDataSet2.lineWidth = 2f
        lineDataSet1.setCircleColor(ContextCompat.getColor(myContext, R.color.yesil))
        lineDataSet2.setCircleColor(ContextCompat.getColor(myContext, R.color.siyah))
        lineDataSet1.valueTextColor = R.color.beyaz
        lineDataSet1.valueTextSize = 12f
        lineDataSet2.valueTextSize = 12f



        gnlineChartView.setDragOffsetX(15f)
        gnlineChartView.setNoDataText("Veri Alınamadı")
        gnlineChartView.setNoDataTextColor(R.color.kirmizi)
        gnlineChartView.setDrawGridBackground(true)
        gnlineChartView.setGridBackgroundColor(ContextCompat.getColor(myContext, R.color.sari))
        gnlineChartView.setBackgroundColor(ContextCompat.getColor(myContext, R.color.beyaz))
        gnlineChartView.setDrawBorders(true)
        gnlineChartView.setBorderWidth(0.3f)



        gnlineChartView.clear()
        gnlineChartView.data = lineData1
        //  gnlineChartView.data = lineData2
        gnlineChartView.invalidate()
        gnlineChartView.animateY(1000)

        gnlineChartView.description.isEnabled = false
        gnlineChartView.axisLeft.isEnabled = false
        gnlineChartView.axisLeft.textSize = 8f
        gnlineChartView.xAxis.textSize = 8f

        gnlineChartView.xAxis.labelCount = ogrEntries.size
        //   gnlineChartView.extraBottomOffset = 8f
        gnlineChartView.xAxis.setDrawAxisLine(false)
        gnlineChartView.xAxis.setCenterAxisLabels(true)

        gnlineChartView.xAxis.isEnabled = false
        gnlineChartView.axisLeft.setEnabled(false)
        gnlineChartView.axisRight.setEnabled(false)


    }


}

private fun showChart(entries: ArrayList<Entry>, gnlineChartView: LineChart, logSicakliklar: ArrayList<Float>, position: Int) {

    //bu detay sayfası ayarları
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

}

