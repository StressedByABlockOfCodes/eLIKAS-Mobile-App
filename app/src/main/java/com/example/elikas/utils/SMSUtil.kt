package com.example.elikas.utils

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast
import java.lang.Exception

class SMSUtil constructor(context: Context){

    private val smgr: SmsManager = SmsManager.getDefault()
    private val mcontext = context

    fun send(msgRequest: String) {
        try {
            smgr.sendTextMessage(
                Constants.globe_labs,
                null,
                msgRequest,
                null,
                null
            )
            Toast.makeText(
                mcontext,
                "SMS Sent Successfully",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                mcontext,
                "SMS Failed to Send, Please try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}