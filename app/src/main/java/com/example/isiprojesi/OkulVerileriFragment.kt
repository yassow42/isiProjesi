package com.example.isiprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_okulverileri.*
import kotlinx.android.synthetic.main.fragment_okulverileri.view.*


class OkulVerileriFragment : Fragment() {



    lateinit var lineData: LineData

    var ref = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_okulverileri, container, false)
        veriAL()
        view.swipeRefreshOkulVerileri.setOnRefreshListener {
            veriAL()
            swipeRefreshOkulVerileri.isRefreshing = false
        }


        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun veriAL() {

        val numaraListesi = ArrayList<Int>()
        val ogrenciOrtSicakliklari = ArrayList<Float>()

        //grafik verileri
        var lineDataSet = LineDataSet(null, null)
        var lineDataSets = ArrayList<LineDataSet>()
        val okulOrtSicakliklari = ArrayList<Float>()
        val okulOrtSicaklikKeyleri = ArrayList<String>()



        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()


        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.child("isi_verileri").hasChildren()) {
                    for (i in p0.child("isi_verileri").children) {
                        numaraListesi.add(i.key.toString().toInt())
                    }

                    for (ds in numaraListesi) {
                        var ortSicaklik = p0.child("isi_verileri").child(ds.toString()).child("ort_sicaklik").value.toString().toFloat()
                        ortSicaklik?.let {
                            ogrenciOrtSicakliklari.add(it)
                        }
                    }

                    for (i in p0.child("ort_okul").children) okulOrtSicaklikKeyleri.add(i.key.toString())

                    for (i in okulOrtSicaklikKeyleri) {
                        okulOrtSicakliklari.add(p0.child("ort_okul").child(i.toString()).child("ort_sicaklik").value.toString().toFloat())
                    }

                    if (okulOrtSicakliklari.size < 10) {
                        for (sayi in 0..okulOrtSicakliklari.size - 1) {
                            entries.add(Entry(sayi.toFloat(), okulOrtSicakliklari[sayi].toFloat()))
                            labels.add((sayi).toString())
                        }
                    } else {
                        for (sayi in okulOrtSicakliklari.size - 7..okulOrtSicakliklari.size - 1) {
                            entries.add(Entry(sayi.toFloat(), okulOrtSicakliklari[sayi].toFloat()))
                            labels.add((sayi).toString())
                        }
                    }

                    showChart(entries, labels,lineDataSet,lineDataSets)


                }

                if (p0.child("ort_okul").hasChildren()) {
                    var ortOkulList = ArrayList<OkulOrtData>()
                    for (ds in p0.child("ort_okul").children) {
                        var gelenData = ds.getValue(OkulOrtData::class.java)!!
                        ortOkulList.add(gelenData)
                    }

                    ortOkulList.sortByDescending { it.olcum_zamani }
                    setupRecyclerView(ortOkulList)
                }

                var zaman = p0.child("zaman").child("gun").value.toString().toLong()
                var suankiZaman = System.currentTimeMillis()

                if (zaman < suankiZaman) {
                    var guncelGece3 = zaman + 86400000
                    ref.child("zaman").child("gun").setValue(guncelGece3)
                    var ortOkulKey = ref.child("ort_okul").push().key.toString()
                    ref.child("ort_okul").child(ortOkulKey).child("ort_sicaklik").setValue(ogrenciOrtSicakliklari.average())
                    ref.child("ort_okul").child(ortOkulKey).child("olcum_zamani").setValue(zaman)
                    ref.child("ort_okul").child(ortOkulKey).child("key").setValue(ortOkulKey)
                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    private fun setupRecyclerView(ortOkulList: ArrayList<OkulOrtData>) {
        rcOkulVerileri.layoutManager = LinearLayoutManager(context!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        val adapter = OrtOkulAdapter(context!!.applicationContext, ortOkulList)
        rcOkulVerileri.adapter = adapter
    }

    private fun showChart(entries: ArrayList<Entry>, labels: ArrayList<String>, lineDataSet: LineDataSet, lineDataSets: ArrayList<LineDataSet>) {
        lineDataSet.setValues(entries)
        lineDataSet.setLabel("Ort. Değerler")
        lineDataSets.clear()
        lineDataSets.add(lineDataSet)
        lineData = LineData(lineDataSets as List<ILineDataSet>?)

        lineDataSet.setColors(ContextCompat.getColor(context!!.applicationContext, R.color.mavi))
        lineDataSet.lineWidth = 2f
        lineDataSet.setCircleColor(ContextCompat.getColor(context!!.applicationContext, R.color.kirmizi))
        lineDataSet.valueTextColor = R.color.mavi
        lineDataSet.valueTextSize = 12f



        lineChartViewOkulVerileri.setDragOffsetX(15f)

        lineChartViewOkulVerileri.setNoDataText("Veri Alınamadı")
        lineChartViewOkulVerileri.setNoDataTextColor(R.color.kirmizi)
        lineChartViewOkulVerileri.setDrawGridBackground(true)
        lineChartViewOkulVerileri.setGridBackgroundColor(ContextCompat.getColor(context!!.applicationContext, R.color.sari))
        lineChartViewOkulVerileri.setBackgroundColor(ContextCompat.getColor(context!!.applicationContext, R.color.beyaz))
        lineChartViewOkulVerileri.setDrawBorders(true)
        lineChartViewOkulVerileri.setBorderWidth(0.3f)


        lineChartViewOkulVerileri.clear()
        lineChartViewOkulVerileri.data = lineData
        lineChartViewOkulVerileri.animateY(1000)

        lineChartViewOkulVerileri.description.isEnabled = false
        // lineChartViewMainActivity.axisLeft.isEnabled = false
        lineChartViewOkulVerileri.axisLeft.textSize = 8f
        lineChartViewOkulVerileri.xAxis.textSize = 8f

        lineChartViewOkulVerileri.xAxis.labelCount = entries.size
        //   gnlineChartView.extraBottomOffset = 8f
        //   lineChartViewMainActivity.xAxis.setDrawAxisLine(false)
        lineChartViewOkulVerileri.xAxis.setCenterAxisLabels(true)

        lineChartViewOkulVerileri.xAxis.isEnabled = false
        lineChartViewOkulVerileri.xAxis.setDrawGridLines(false)
        lineChartViewOkulVerileri.xAxis.setDrawAxisLine(false)
        lineChartViewOkulVerileri.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //lineChartViewMainActivity.axisLeft.setEnabled(false) lineChartViewMainActivity.axisLeft.setDrawLabels(false)


        lineChartViewOkulVerileri.axisRight.setEnabled(false)




        lineChartViewOkulVerileri.invalidate()

    }

}