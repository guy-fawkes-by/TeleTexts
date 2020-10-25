package com.ibashkimi.telegram.sms

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.provider.Settings.Global.getString
import android.telephony.SmsMessage
import android.util.Log
import androidx.annotation.RequiresApi
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.Repository

class SmsBroadcastReceiver : BroadcastReceiver() {
    final var SMS_BUNDLE = "pdus"

    fun getSharedPreferences(context: Context?, key: Int) : String? {
        val sharedPref = context?.getSharedPreferences(context.getString(R.string.settings), Context.MODE_PRIVATE)
        return sharedPref?.getString(context.getString(key), null);
    }

    @SuppressLint("NewApi")
    override fun onReceive(context: Context?, intent: Intent?) {
        val intentExtras = intent?.extras;
        if (intentExtras != null) {
            val targetPhoneNumber = context?.getString(R.string.target_phone_number)//this.getSharedPreferences(context, R.string.target_phone_number)
            val smsBundle = intentExtras.get(SMS_BUNDLE) as Array<Any>
            val format  = intentExtras.getString("format");
            val isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ;
            val msgs = arrayListOf<SmsMessage>();

            for (msg in smsBundle) {
                var sms : SmsMessage
                if (isVersionM) {
                    sms = SmsMessage.createFromPdu(msg as ByteArray, format)

                } else {
                    sms = SmsMessage.createFromPdu(msg as ByteArray)
                }
                // parse sms
                if (sms.getOriginatingAddress() == targetPhoneNumber) {
                    msgs.add(sms)
                }
                Log.d("SMS onReceive: ", sms.messageBody);
            }
            if (msgs.size > 0) {
                val pendingResult: PendingResult = goAsync()
                val asyncTask = Task(pendingResult, context, msgs)
                asyncTask.execute()
            }
        }
    }


    private class Task(
        private val pendingResult: PendingResult,
        private val context: Context?,
        private val messages: ArrayList<SmsMessage>
    ) : AsyncTask<String, Int, String>() {

        fun setSharedPreferences(key: Int, value: Int) {
            val sharedPref = context?.getSharedPreferences(context.getString(R.string.settings), Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString(context.getString(key), context.getString(value))
                apply()
            }
        }



        override fun doInBackground(vararg params: String?): String {
            for (msg in messages) {
                if (msg.messageBody.contains(context?.getString(R.string.start_command).toString(), true)) {
                    setSharedPreferences(R.string.status, R.string.active)
                } else if (msg.messageBody.contains(context?.getString(R.string.stop_commang).toString(), true)) {
                    setSharedPreferences(R.string.status, R.string.idle)
                }
            }
            return toString().also { log ->
                Log.d("hui", log)
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish()
        }
    }
}