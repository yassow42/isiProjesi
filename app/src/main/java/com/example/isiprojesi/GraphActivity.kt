package com.example.isiprojesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_graph.*


class GraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        entries.add(Entry(2f, 3f))
        entries.add(Entry(3f, 4f))
        entries.add(Entry(2f, 4f))
        entries.add(Entry(3f, 8f))
        entries.add(Entry(1f, 12f))
        entries.add(Entry(1f, -12f))

        labels.add("1")
        labels.add("2")
        labels.add("3")
        labels.add("4")
        labels.add("5")
        labels.add("6")

        val v1 = LineDataSet(entries, "Sıcaklıklar")
        val data = LineData(v1)
          v1.setDrawValues(false)
          v1.setDrawFilled(true)
        lineChartViewGraph.data = data // set the data and list of lables into chart
        lineChartViewGraph.description.text = "Days"
        lineChartViewGraph.setNoDataText("No forex yet!")
    }
}