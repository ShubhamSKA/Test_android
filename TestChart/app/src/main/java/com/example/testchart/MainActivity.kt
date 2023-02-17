package com.example.testchart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstLineData: MutableList<Entry> = mutableListOf()
        for (i in 0..30) {
            var xTemp = i.toFloat()
            var yTemp = ((2*i*i)-(7*i)+3).toFloat()
            var lineData: Entry = Entry((xTemp), (yTemp))
            firstLineData.add(lineData)
        }
        val firstLineDataSet: LineDataSet = LineDataSet(firstLineData, "Equation 1")
        val lineChart: LineChart = findViewById(R.id.mainLineChart)
        lineChart.data = LineData(firstLineDataSet)
        lineChart.axisLeft.axisMaximum = lineChart.data.yMax + 10f
        lineChart.axisRight.axisMaximum = lineChart.data.yMin - 10f
        lineChart.animateX(100)

    }
}