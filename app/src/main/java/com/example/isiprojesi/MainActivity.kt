package com.example.isiprojesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val numaraListesi = ArrayList<Int>()
    lateinit var adapter: MainAdapter

    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        veriAL()
        makineAktiflikleriKontrol()
        butonlar()
//dialog ile hangı makıneye baktıgı cıksın mak1e bakan sadece o makıneye veri girsin
        setupRecyclerView()
        refreshLayout.setOnRefreshListener {
            veriAL()
            refreshLayout.isRefreshing = false
        }
    }

    private fun setupRecyclerView() {
        rcOgrenciler.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        adapter = MainAdapter(this@MainActivity, numaraListesi)
        rcOgrenciler.adapter = adapter
    }

    private fun makineAktiflikleriKontrol() {
        ref.child("mak_aktiflikleri").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    var mak1AktifMi = p0.child("Mak1").value.toString()
                    if (mak1AktifMi == "Aktif") {
                           imgMak1.visibility = View.VISIBLE
                        textInputLayoutMakine1.visibility = View.VISIBLE
                        etOgrenciNo1.visibility = View.VISIBLE
                        tvMak1Sicaklik.visibility = View.VISIBLE
                    } else {
                        imgMak1.visibility = View.VISIBLE
                        textInputLayoutMakine1.visibility = View.GONE
                        etOgrenciNo1.visibility = View.GONE
                        tvMak1Sicaklik.visibility = View.GONE

                        etOgrenciNo1.text!!.clear()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


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


        ref.child("Makineler/Mak1_Sicaklik").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvMak1Sicaklik.setText("Mak1: " + snapshot.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun butonlar() {

        etOgrenciNo1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                imgMak1.visibility = View.VISIBLE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length > 0) {

                    imgMak1.visibility = View.VISIBLE

                } else {
                    imgMak1.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        imgMak1.setOnClickListener {
            ref.child("Makineler").child("Mak1").setValue(etOgrenciNo1.text.toString())
            // ref.child("mak_aktiflikleri").child("Mak1").setValue("Aktif Değil")
            imgMak1.visibility = View.GONE
            textInputLayoutMakine1.visibility = View.GONE
            tvMak1Sicaklik.visibility = View.GONE
        }
    }

}