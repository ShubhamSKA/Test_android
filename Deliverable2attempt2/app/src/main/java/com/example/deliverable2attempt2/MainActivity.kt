package com.example.deliverable2attempt2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val price: TextView=findViewById(R.id.tipamount)

        val priceInput: EditText=findViewById(R.id.Price_demand)
        val tipInput: EditText=findViewById(R.id.personalized_tip)

        val tip10: Button = findViewById(R.id.tip10)
        val tip25: Button = findViewById(R.id.tip25)
        val tip18: Button = findViewById(R.id.tip18)
        val calcButton: Button = findViewById(R.id.calc_button)


        tip10.setOnClickListener { tipInput.setText("10") }
        tip18.setOnClickListener { tipInput.setText("18") }
        tip25.setOnClickListener { tipInput.setText("25") }
        calcButton.setOnClickListener{
            var initialPrice = priceInput.text.toString().toFloat()
            var tipValue = tipInput.text.toString().toFloat()
            var finalPrice = (initialPrice*tipValue/100.0)

            price.text=("$${String.format("%.2f",finalPrice)}")
            Toast.makeText(applicationContext, R.string.completion, Toast.LENGTH_LONG).show()
        }
    }
}