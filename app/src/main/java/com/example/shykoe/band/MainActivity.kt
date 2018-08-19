package com.example.shykoe.band

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    var mBtAdapter : BluetoothAdapter ?= null
    var config : Config ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBtAdapter == null){
            Toast.makeText(this,"BlueTooth is not available", Toast.LENGTH_LONG).show()
            finish()
        }
        config = Config(this)
        if(config!!.valid== true){
            if(!mBtAdapter!!.isEnabled())
                mBtAdapter!!.enable()
        }
    fun serverInit(){
        var bindIntent : Intent = Intent(this,this.javaClass)
    }
    }
}
