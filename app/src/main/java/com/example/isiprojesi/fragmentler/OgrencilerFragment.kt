package com.example.isiprojesi.fragmentler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isiprojesi.OgrAdapter
import com.example.isiprojesi.R
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_ogrenciler.view.*


class OgrencilerFragment : Fragment() {



    lateinit var lineData: LineData

    val ref = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ogrenciler, container, false)


        veriAL(view)
        view.refreshLayoutOgr.setOnRefreshListener {
            veriAL(view)
            view.refreshLayoutOgr.isRefreshing = false
        }


        return view
    }

    private fun veriAL(view: View) {

        val numaraListesi = ArrayList<Int>()
        val ogrenciOrtSicakliklari = ArrayList<Float>()
        val okulOrtSicakliklari = ArrayList<Float>()
        //grafik verileri
        var lineDataSet = LineDataSet(null, null)
        var lineDataSets = ArrayList<LineDataSet>()

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
                        ogrenciOrtSicakliklari.add(p0.child("isi_verileri").child(ds.toString()).child("ort_sicaklik").value.toString().toFloat())
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

                    showChart(view, entries, labels,lineDataSet,lineDataSets)
                    setupRecyclerView(view,numaraListesi,okulOrtSicakliklari)

                }


            }

            override fun onCancelled(p0: DatabaseError) {



            }
        })


    }


    private fun setupRecyclerView(view: View, numaraListesi: ArrayList<Int>, okulOrtSicakliklari: ArrayList<Float>) {
        view.rcOgrFragment.layoutManager = LinearLayoutManager(context!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        val adapter = OgrAdapter(context!!.applicationContext, numaraListesi, okulOrtSicakliklari)
        view.rcOgrFragment.adapter = adapter
    }

    private fun showChart(view: View, entries: ArrayList<Entry>, labels: ArrayList<String>, lineDataSet: LineDataSet, lineDataSets: ArrayList<LineDataSet>) {
        lineDataSet.setValues(entries)
        lineDataSet.setLabel("Ort. Değerler")
        lineDataSets.clear()
        lineDataSets.add(lineDataSet)
        lineData = LineData(lineDataSets as List<ILineDataSet>?)

        lineDataSet.setColors(ContextCompat.getColor(context!!.applicationContext, R.color.mavi))
        lineDataSet.lineWidth = 2f
        lineDataSet.setCircleColor(ContextCompat.getColor(context!!.applicationContext, R.color.kirmizi))
        // lineDataSet.valueTextColor = R.color.beyaz
        lineDataSet.valueTextSize = 0f

        view.lineChartOgrFragment

        view.lineChartOgrFragment.setDragOffsetX(15f)

        view.lineChartOgrFragment.setNoDataText("Veri Alınamadı")
        view.lineChartOgrFragment.setNoDataTextColor(R.color.kirmizi)
        view.lineChartOgrFragment.setDrawGridBackground(true)
        view.lineChartOgrFragment.setGridBackgroundColor(ContextCompat.getColor(context!!.applicationContext, R.color.sari))
        view.lineChartOgrFragment.setBackgroundColor(ContextCompat.getColor(context!!.applicationContext, R.color.beyaz))
        view.lineChartOgrFragment.setDrawBorders(true)
        view.lineChartOgrFragment.setBorderWidth(0.3f)


        view.lineChartOgrFragment.clear()
        view.lineChartOgrFragment.data = lineData
        view.lineChartOgrFragment.animateY(1000)

        view.lineChartOgrFragment.description.isEnabled = false
        // lineChartViewMainActivity.axisLeft.isEnabled = false
        view.lineChartOgrFragment.axisLeft.textSize = 8f
        view.lineChartOgrFragment.xAxis.textSize = 8f

        view.lineChartOgrFragment.xAxis.labelCount = entries.size
        //   gnlineChartView.extraBottomOffset = 8f
        //   lineChartViewMainActivity.xAxis.setDrawAxisLine(false)
        view.lineChartOgrFragment.xAxis.setCenterAxisLabels(true)

        view.lineChartOgrFragment.xAxis.isEnabled = false
        view.lineChartOgrFragment.xAxis.setDrawGridLines(false)
        view.lineChartOgrFragment.xAxis.setDrawAxisLine(false)
        view.lineChartOgrFragment.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //lineChartViewMainActivity.axisLeft.setEnabled(false) lineChartViewMainActivity.axisLeft.setDrawLabels(false)

        view.lineChartOgrFragment.axisRight.setEnabled(false)

        view.lineChartOgrFragment.invalidate()

    }


}