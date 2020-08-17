package com.gopi.unitechbarcodescanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var tag: String = MainActivity::class.java.simpleName
    private val SCANNER_INIT = "unitech.scanservice.init"
    private val SCAN2KEY_SETTING = "unitech.scanservice.scan2key_setting"
    private val START_SCANSERVICE = "unitech.scanservice.start"
    private val CLOSE_SCANSERVICE = "unitech.scanservice.close"
    private val SOFTWARE_SCANKEY = "unitech.scanservice.software_scankey"
    private val ACTION_RECEIVE_DATA = "unitech.scanservice.data"
    private val ACTION_RECEIVE_DATABYTES = "unitech.scanservice.databyte"
    private val ACTION_RECEIVE_DATALENGTH = "unitech.scanservice.datalength"
    private val ACTION_RECEIVE_DATATYPE = "unitech.scanservice.datatype"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        registerScannerReceiver()
    }

    private fun initView(){
        //scan button action
        scanBtn.setOnClickListener {
            callScanner()
        }
    }

    private fun registerScannerReceiver() {
        //register receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_RECEIVE_DATA)
        intentFilter.addAction(ACTION_RECEIVE_DATABYTES)
        intentFilter.addAction(ACTION_RECEIVE_DATALENGTH)
        intentFilter.addAction(ACTION_RECEIVE_DATATYPE)
        registerReceiver(mScanReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(tag, "onDestroy()")
        closeScanService()
        unregisterReceiver(mScanReceiver)
    }

    private fun callScanner() {
        Log.v(tag, "callScanner()")
        progressBar.visibility = View.VISIBLE

        startScanService()

        //Must set scan2key to false in order for data to send through intent. Otherwise
        //data will sent to keyboard buffer.
        setScan2Key()

        //set init intent
        setInit()

        //start scanning
        val bundle = Bundle()
        bundle.putBoolean("scan", true)
        val mIntent = Intent().setAction(SOFTWARE_SCANKEY).putExtras(bundle)
        sendBroadcast(mIntent)
    }

    private fun setScan2Key() {
        //which supports keyboard emulation features
        val bundle = Bundle()
        bundle.putBoolean("scan2key", false)
        val mIntent = Intent().setAction(SCAN2KEY_SETTING).putExtras(bundle)
        sendBroadcast(mIntent)
    }

    private fun setInit() {
        //init the scanner
        val bundle1 = Bundle()
        bundle1.putBoolean("enable", true)
        val mIntent1 = Intent().setAction(SCANNER_INIT).putExtras(bundle1)
        sendBroadcast(mIntent1)
    }

    private fun startScanService() {
        //to start scan service
        val bundle = Bundle()
        bundle.putBoolean("close", true)
        val mIntent = Intent().setAction(START_SCANSERVICE).putExtras(bundle)
        sendBroadcast(mIntent)
    }

    private fun closeScanService() {
        //to close scan service
        val bundle = Bundle()
        bundle.putBoolean("close", true)
        val mIntent = Intent().setAction(CLOSE_SCANSERVICE).putExtras(bundle)
        sendBroadcast(mIntent)
    }

    private val mScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.v(tag, "onReceive()")
            val action = intent.action

            val bundle = intent.extras ?: return
            when (action) {
                ACTION_RECEIVE_DATA -> {
                    Log.v(tag, "ACTION_RECEIVE_DATA")
                    val barcodeStr = bundle.getString("text")
                    Log.v(tag, "barcode data: $barcodeStr")

                    progressBar.visibility = View.GONE

                    tvScannedItem.text = ""
                    tvScannedItem.text = barcodeStr!!
                }
            }
        }
    }
}
