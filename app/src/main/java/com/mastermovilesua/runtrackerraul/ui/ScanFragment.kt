package com.mastermovilesua.runtrackerraul.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mastermovilesua.runtrackerraul.adapter.DeviceAdapter
import com.mastermovilesua.runtrackerraul.bluetooth.BleManager
import com.mastermovilesua.runtrackerraul.databinding.FragmentScanBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanFragment : Fragment(), DeviceAdapter.OnItemClickListener {

    private lateinit var bleManager: BleManager
    private lateinit var binding: FragmentScanBinding
    private val scanResults = mutableListOf<String>()
    private lateinit var deviceAdapter: DeviceAdapter
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bleManager = BleManager(requireContext())
        deviceAdapter = DeviceAdapter(scanResults, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanBinding.inflate(inflater, container, false)

        binding.scanButton.setOnClickListener {
            startScanning()
        }
        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun startScanning() {
        scanResults.clear()
        deviceAdapter.updateDevices(scanResults)
        bleManager.startScan()
        binding.scanProgressBar.visibility = View.VISIBLE

        showScanResultsDialog()

        lifecycleScope.launch {
            bleManager.scanResults.collect { devices ->
                withContext(Dispatchers.Main) {
                    val newScanResults = devices.map { "${it.name ?: "Unknown"} - ${it.address}" }

                    // Update only if there are new devices
                    if (newScanResults != scanResults) {
                        scanResults.clear()
                        scanResults.addAll(newScanResults)
                        deviceAdapter.updateDevices(scanResults)
                    }

                    binding.scanProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showScanResultsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Device")

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceAdapter
        }

        builder.setView(recyclerView)
        builder.setNegativeButton("Cancel") { dialog, _ ->
            bleManager.stopScan()
            dialog.dismiss()
        }

        dialog = builder.create()
        dialog?.show()
    }

    override fun onItemClick(deviceInfo: String) {
        val deviceAddress = deviceInfo.split(" - ")[1]
        bleManager.connectToDevice(requireContext(), deviceAddress)
        binding.deviceInfoText.text = deviceInfo
        dialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        bleManager.stopScan()
    }
}
