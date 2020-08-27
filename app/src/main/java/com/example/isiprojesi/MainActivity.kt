package com.example.isiprojesi

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.hangi_makine.*
import kotlinx.android.synthetic.main.numara_girme.*

class MainActivity : AppCompatActivity() {
    val numaraListesi = ArrayList<Int>()
    lateinit var adapter: MainAdapter

    val ref = FirebaseDatabase.getInstance().reference


    var makineSecildimi = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
        var bottomSheetDialogNo = Dialog(this@MainActivity, android.R.style.Theme_Light)

        bottomSheetDialogNo.setTitle("Öğrenci No Giriniz: ")

        bottomSheetDialogNo.setOnDismissListener {
            Log.e("sad", "kapatıldı")
        }
        // bottomSheetDialogNo.setCancelable(false)
        //  bottomSheetDialogNo.setContentView(R.layout.numara_girme)
        //   var viewBottom = bottomSheetDialogNo.layoutInflater.inflate(R.layout.bottom_sheet_dialog,R.style.Theme_Design_BottomSheetDialog)
        //   bottomSheetDialogNo.setContentView(viewBottom)
        bottomSheetDialogNo.setContentView(R.layout.numara_girme)


        if (makineSecildimi) {

            var secilenMakine = prefences.getString("Seçilen Makine", "Makine Boş")
            Log.e("sad", secilenMakine.toString())

            ref.child("mak_aktiflikleri").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        var mak1AktifMi = p0.child("Mak1").value.toString()

                        Log.e("sad2", mak1AktifMi + "  " + secilenMakine)
                        if (mak1AktifMi == "Aktif" && secilenMakine == "Makine 1") {
                            bottomSheetDialogNo.show()

                            ref.child("Makineler/Mak1_Sicaklik").addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    bottomSheetDialogNo.tvMak1Sicaklik.setText("Mak1: " + snapshot.value.toString())
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                            bottomSheetDialogNo.mak1LinearLay.visibility = View.VISIBLE
                        } else {
                            bottomSheetDialogNo.mak1LinearLay.visibility = View.GONE
                        }

                        if (mak1AktifMi == "Aktif" && secilenMakine == "Makine 2") {
                            bottomSheetDialogNo.show()

                            ref.child("Makineler/Mak2_Sicaklik").addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    bottomSheetDialogNo.tvMak2Sicaklik.setText("Mak2: " + snapshot.value.toString())
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                            bottomSheetDialogNo.mak2LinearLay.visibility = View.VISIBLE
                        } else {

                            bottomSheetDialogNo.mak2LinearLay.visibility = View.GONE
                        }

                        if (mak1AktifMi == "Aktif" && secilenMakine == "Makine 3") {
                            bottomSheetDialogNo.show()

                            ref.child("Makineler/Mak3_Sicaklik").addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    bottomSheetDialogNo.tvMak3Sicaklik.setText("Mak3: " + snapshot.value.toString())
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                            bottomSheetDialogNo.mak3LinearLay.visibility = View.VISIBLE
                        } else {

                            bottomSheetDialogNo.mak3LinearLay.visibility = View.GONE
                        }


                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        }

    }


    private fun veriAL() {


        ref.child("isi_verileri").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                numaraListesi.clear()
                if (p0.hasChildren()) {
                    for (i in p0.children) {
                        numaraListesi.add(i.key.toString().toInt())
                    }
                }

                adapter.notifyDataSetChanged()


            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


    }

    private fun setupRecyclerView() {
        rcOgrenciler.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        adapter = MainAdapter(this@MainActivity, numaraListesi)
        rcOgrenciler.adapter = adapter
    }


}