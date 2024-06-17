package com.mastermovilesua.runtrackerraul.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mastermovilesua.runtrackerraul.R

class DeviceAdapter(private val devices: MutableList<String>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(deviceInfo: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val deviceInfo = devices[position]
        val parts = deviceInfo.split(" - ")

        holder.deviceName.text = parts[0]
        holder.deviceAddress.text = parts[1]

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(deviceInfo)
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    fun updateDevices(newDevices: List<String>) {
        newDevices.forEach {
            if (!devices.contains(it)){
                devices.add(it)

            }
        }
        notifyDataSetChanged()
    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.device_name)
        val deviceAddress: TextView = itemView.findViewById(R.id.device_address)
    }
}



