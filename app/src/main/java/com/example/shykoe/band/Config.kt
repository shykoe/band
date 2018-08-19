package com.example.shykoe.band

import android.R.attr.editTextBackground
import android.content.SharedPreferences
import android.content.Context;
import android.content.SharedPreferences.Editor
import android.R.attr.name




class Config {
    var name : String ?= null
    var addr : String ?= null
    var valid : Boolean ?= false
    val KEY_NAME = "name"
    val KEY_ADDR = "addr"
    val KEY_VALID = "valid"
    val DATABASE = "Config"
    var context: Context? = null
    private var sp: SharedPreferences? = null
    constructor(context: Context){
        this.context = context
        sp = this.context?.getSharedPreferences(DATABASE,Context.MODE_PRIVATE)
        valid = sp?.getBoolean(KEY_VALID,false)
        name = sp?.getString(KEY_NAME,"")
        addr = sp?.getString(KEY_ADDR,"")
    }
    fun clear_Config(){
        valid = false
        var et = sp!!.edit()
        et.clear()
        et.commit()
    }
    fun save_Config(ble_name:String, ble_addr: String){
        valid = true
        name = ble_name
        addr = ble_addr
        var et = sp!!.edit()
        et.putString(KEY_ADDR,addr)
        et.putString(KEY_NAME,name)
        et.putBoolean(KEY_VALID,true)
    }

}