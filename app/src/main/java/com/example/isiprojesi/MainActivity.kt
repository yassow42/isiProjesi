package com.example.isiprojesi

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.hangi_makine.*
import kotlinx.android.synthetic.main.numara_girme.view.*

class MainActivity : AppCompatActivity() {
    val numaraListesi = ArrayList<Int>()
    val ogrenciOrtSicakliklari = ArrayList<Float>()

    //grafik verileri
    var lineDataSet = LineDataSet(null, null)
    var lineDataSets = ArrayList<LineDataSet>()
    val okulOrtSicakliklari = ArrayList<Float>()
    val okulOrtSicaklikKeyleri = ArrayList<String>()

    lateinit var lineData: LineData


    lateinit var adapter: OgrAdapter
    val ref = FirebaseDatabase.getInstance().reference


    var makineSecildimi = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)



        val prefences = getSharedPreferences("hangiMak", Context.MODE_PRIVATE)
        val editor = prefences.edit()

        Log.e("OnCreate", prefences.getString("Seçilen Makine", "Makine Seçilmedi").toString())
        hangiMakcalisiyrsun(prefences, editor)

        veriAL()

        //dialog ile hangı makıneye baktıgı cıksın mak1e bakan sadece o makıneye veri girsin
        setupRecyclerView()
        refreshLayout.setOnRefreshListener {
            veriAL()
            refreshLayout.isRefreshing = false
        }
    }

    private fun hangiMakcalisiyrsun(preferences: SharedPreferences, editor: SharedPreferences.Editor) {


        var bottomSheetDialog = Dialog(this)
        //   var viewBottom = bottomSheetDialog.layoutInflater.inflate(R.layout.bottom_sheet_dialog,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        // bottomSheetDialog.setContentView(viewBottom)
        bottomSheetDialog.setContentView(R.layout.hangi_makine)

        bottomSheetDialog.makbos.setOnClickListener {
            editor.putString("Seçilen Makine", "Makine Boş")
            editor.apply()
            makineSecildimi = true

            bottomSheetDialog.dismiss()
            makineAktiflikleriKontrol(preferences, editor)
        }
        bottomSheetDialog.mak1.setOnClickListener {
            editor.putString("Seçilen Makine", "Makine 1")
            editor.apply()
            makineSecildimi = true

            makineAktiflikleriKontrol(preferences, editor)

            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.mak2.setOnClickListener {
            editor.putString("Seçilen Makine", "Makine 2")
            editor.apply()
            makineSecildimi = true
            makineAktiflikleriKontrol(preferences, editor)

            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.mak3.setOnClickListener {
            editor.putString("Seçilen Makine", "Makine 3")
            editor.apply()
            makineSecildimi = true
            makineAktiflikleriKontrol(preferences, editor)

            bottomSheetDialog.dismiss()

        }

        bottomSheetDialog.show()


    }


    private fun makineAktiflikleriKontrol(prefences: SharedPreferences, editor: SharedPreferences.Editor) {

        val mDialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.numara_girme, null)
        val mBuilder = AlertDialog.Builder(this@MainActivity)
            .setView(mDialogView)
            .setTitle("Öğrenci No Giriniz: ").create()

        mDialogView.imgCancel.setOnClickListener {
            mDialogView.etOgrenciNo1.text!!.clear()
            mBuilder.dismiss()
        }

        if (makineSecildimi) {

            var secilenMakine = prefences.getString("Seçilen Makine", "Makine Boş")
            ref.child("mak_aktiflikleri").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        var mak1AktifMi = p0.child("Mak1").value.toString()

                        if (mak1AktifMi == "Aktif" && secilenMakine == "Makine 1") {
                            mBuilder.show()
                            mDialogView.etOgrenciNo1.text!!.clear()
                            mDialogView.mak1LinearLay.visibility = View.VISIBLE

                            Log.e("no list", numaraListesi.size.toString())
                            mDialogView.imgOnay.setOnClickListener {
                                var girilenOgrNo = mDialogView.etOgrenciNo1.text.toString()

                                var girilenNoVarmi = linearSearch(numaraListesi, girilenOgrNo)
                                if (girilenOgrNo!!.isNotEmpty() && girilenNoVarmi == 1) {
                                    ref.child("Makineler/Mak1").setValue(girilenOgrNo.toString())
                                    mDialogView.etOgrenciNo1.text!!.clear()
                                } else if (girilenNoVarmi == -1) {
                                    ref.child("Makineler/Mak1").setValue(girilenOgrNo.toString())
                                    ref.child("isi_verileri").child(girilenOgrNo).child("ort_sicaklik").setValue(36)
                                }

                                mBuilder.dismiss()
                            }


                        } else {
                            mDialogView.mak1LinearLay.visibility = View.GONE
                        }
////////////////////////////////////////////////////////////////////////////////////////////////////MAKİNE2/////////////////////////////////////////////////////////
                        if (mak1AktifMi == "Aktif" && secilenMakine == "Makine 2") {
                            mBuilder.show()
                            mDialogView.etOgrenciNo2.text!!.clear()
                            mDialogView.mak2LinearLay.visibility = View.VISIBLE


                        } else {

                            mDialogView.mak2LinearLay.visibility = View.GONE
                        }
                        if (mak1AktifMi == "Aktif" && secilenMakine == "Makine 3") {
                            mDialogView.mak3LinearLay.visibility = View.VISIBLE
                        } else {
                            mDialogView.mak3LinearLay.visibility = View.GONE
                        }

                    }
                    ref.child("Makineler").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            mDialogView.tvMak1Sicaklik.setText(p0.child("Mak1_Sicaklik").value.toString())
                            mDialogView.tvMak2Sicaklik.setText(p0.child("Mak2_Sicaklik").value.toString())
                            mDialogView.tvMak3Sicaklik.setText(p0.child("Mak3_Sicaklik").value.toString())
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun linearSearch(numaraListesi: ArrayList<Int>, girilenOgrNo: String): Int { //linearSearch metotumuz
        for (i in numaraListesi) {
            if (i == girilenOgrNo.toInt()) {
                return 1
            }
        }
        return -1
    }

    private fun veriAL() {

        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()


        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                numaraListesi.clear()
                if (p0.child("isi_verileri").hasChildren()) {
                    for (i in p0.child("isi_verileri").children) {
                        numaraListesi.add(i.key.toString().toInt())
                    }
                    adapter.notifyDataSetChanged()
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

                    showChart(entries, labels)


                }



            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")


            }
        })

        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                var gece3 = p0.child("zaman").child("gun").value.toString().toLong()
                var suankiZaman = System.currentTimeMillis()

                if (gece3 < suankiZaman) {
                    var guncelGece3 = gece3 + 86400000
                    ref.child("zaman").child("gun").setValue(guncelGece3)
                    var ortOkulKey = ref.child("ort_okul").push().key.toString()
                    ref.child("ort_okul").child(ortOkulKey).child("ort_sicaklik").setValue(ogrenciOrtSicakliklari.average())
                    ref.child("ort_okul").child(ortOkulKey).child("olcum_zaman").setValue(ogrenciOrtSicakliklari.average())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }


    private fun setupRecyclerView() {
        rcOgrenciler.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        adapter = OgrAdapter(this@MainActivity, numaraListesi, okulOrtSicakliklari)
        rcOgrenciler.adapter = adapter
    }

    private fun showChart(entries: ArrayList<Entry>, labels: ArrayList<String>) {
        lineDataSet.setValues(entries)
        lineDataSet.setLabel("Ort. Değerler")
        lineDataSets.clear()
        lineDataSets.add(lineDataSet)
        lineData = LineData(lineDataSets as List<ILineDataSet>?)

        lineDataSet.setColors(ContextCompat.getColor(this, R.color.mavi))
        lineDataSet.lineWidth = 2f
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.kirmizi))
       // lineDataSet.valueTextColor = R.color.beyaz
        lineDataSet.valueTextSize = 0f



        lineChartViewMainActivity.setDragOffsetX(15f)

        lineChartViewMainActivity.setNoDataText("Veri Alınamadı")
        lineChartViewMainActivity.setNoDataTextColor(R.color.kirmizi)
        lineChartViewMainActivity.setDrawGridBackground(true)
        lineChartViewMainActivity.setGridBackgroundColor(ContextCompat.getColor(this, R.color.sari))
        lineChartViewMainActivity.setBackgroundColor(ContextCompat.getColor(this, R.color.beyaz))
        lineChartViewMainActivity.setDrawBorders(true)
        lineChartViewMainActivity.setBorderWidth(0.3f)


        lineChartViewMainActivity.clear()
        lineChartViewMainActivity.data = lineData
        lineChartViewMainActivity.animateY(1000)

        lineChartViewMainActivity.description.isEnabled = false
        // lineChartViewMainActivity.axisLeft.isEnabled = false
        lineChartViewMainActivity.axisLeft.textSize = 8f
        lineChartViewMainActivity.xAxis.textSize = 8f

        lineChartViewMainActivity.xAxis.labelCount = entries.size
        //   gnlineChartView.extraBottomOffset = 8f
        //   lineChartViewMainActivity.xAxis.setDrawAxisLine(false)
        lineChartViewMainActivity.xAxis.setCenterAxisLabels(true)

        lineChartViewMainActivity.xAxis.isEnabled = false
        lineChartViewMainActivity.xAxis.setDrawGridLines(false)
        lineChartViewMainActivity.xAxis.setDrawAxisLine(false)
        lineChartViewMainActivity.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //lineChartViewMainActivity.axisLeft.setEnabled(false) lineChartViewMainActivity.axisLeft.setDrawLabels(false)


        lineChartViewMainActivity.axisRight.setEnabled(false)




        lineChartViewMainActivity.invalidate()

    }




}