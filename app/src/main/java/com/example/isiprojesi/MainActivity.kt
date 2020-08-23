package com.example.isiprojesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val numaraListesi = ArrayList<Int>()


    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        veriAL()
        makineAktiflikleriKontrol()
        butonlar()

        refreshLayout.setOnRefreshListener {
            veriAL()
            refreshLayout.isRefreshing = false
        }
    }

    private fun makineAktiflikleriKontrol() {
        ref.child("mak_aktiflikleri").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    if (p0.child("Mak1").value.toString() == "Aktif") {
                        //    imgMak1.visibility = View.VISIBLE
                        textInputLayoutMakine1.visibility = View.VISIBLE
                    } else {
                        imgMak1.visibility = View.GONE
                        textInputLayoutMakine1.visibility = View.GONE
                        etOgrenciNo1.text!!.clear()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        ref.child("Makineler").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    var mak1 = p0.child("Mak1").value.toString()
                    if (mak1 == "0") {
                        imgMak1.visibility = View.GONE
                        textInputLayoutMakine1.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun butonlar() {

        etOgrenciNo1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                imgMak1.visibility = View.GONE
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
            imgMak1.visibility = View.GONE
            textInputLayoutMakine1.visibility = View.GONE
        }
    }


    private fun veriAL() {

        rcOgrenciler.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        val adapter = MainAdapter(this@MainActivity, numaraListesi)
        rcOgrenciler.adapter = adapter

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


}