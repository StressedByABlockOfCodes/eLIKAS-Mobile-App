package com.example.elikas.utils

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast
import android.content.Intent

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.app.Activity
import android.content.IntentFilter


class SMSUtil constructor(context: Context){

    private val smgr: SmsManager = SmsManager.getDefault()
    private val mcontext = context
    private val SENT = "SMS_SENT"
    private val DELIVERED = "SMS_DELIVERED"

    fun send(msgRequest: String) {
        val sentPI = PendingIntent.getBroadcast(mcontext, 0, Intent(SENT), 0) //FLAG_UPDATE_CURRENT
        val deliveredPI = PendingIntent.getBroadcast(mcontext, 0, Intent(DELIVERED), 0)
        try {
            // ---Notify when the SMS has been sent---
            //mcontext.registerReceiver(sendReceiver(), IntentFilter(SENT))
            // ---Notify when the SMS has been delivered---
            //mcontext.registerReceiver(deliverReceiver(), IntentFilter(DELIVERED))
            //smgr.sendTextMessage(Constants.globe_labs, null, msgRequest, sentPI, deliveredPI)

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

    private fun sendReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        mcontext, "SMS sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
                        mcontext, "Generic failure",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
                        mcontext, "No service",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
                        mcontext, "Null PDU",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
                        mcontext, "Radio off",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun deliverReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        mcontext, "SMS delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(
                        mcontext, "SMS not delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}