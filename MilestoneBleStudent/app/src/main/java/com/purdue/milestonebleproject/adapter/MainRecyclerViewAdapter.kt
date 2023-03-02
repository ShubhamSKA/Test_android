package com.purdue.milestonebleproject.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purdue.milestonebleproject.R

/**
 * This is an Adapter class for BluetoothScanActivity. Please do not modify the code.
 * @author Doyoon Kim (kim3312@purdue.edu)
 * @version Initial (October 21, 2022)
 * @suppress missing permission
 */
@SuppressLint("MissingPermission")
class MainRecyclerViewAdapter(listener: ItemClickListener)
    : RecyclerView.Adapter<MainRecyclerViewAdapter.MainViewHolder>() {

    private var clickListener: ItemClickListener = listener
    val bluetoothDevice = mutableListOf<BluetoothDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return MainViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.deviceNameView.text = bluetoothDevice[position].name ?: "Unknown Device"
        holder.deviceAddressView.text = bluetoothDevice[position].address

    }

    override fun getItemCount(): Int {
        return bluetoothDevice.size
    }

    class MainViewHolder (val view: View, val clickListener: ItemClickListener)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        var deviceNameView = view.findViewById<TextView>(R.id.deviceNameTextView)
        val deviceAddressView = view.findViewById<TextView>(R.id.deviceAddressTextView)
        val connectButton = view.findViewById<Button>(R.id.btnConnect)

        // Assign OnClickListener to this view
        init {
            connectButton.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            clickListener.onClick(view, adapterPosition)
        }


    }
}

interface ItemClickListener {
    fun onClick(v: View, position: Int)
}