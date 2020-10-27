package com.ibashkimi.telegram.sms

import android.R.attr.phoneNumber
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.ibashkimi.telegram.data.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "com.ibashkimi.telegram.sms.action.FOO"
private const val ACTION_BAZ = "com.ibashkimi.telegram.sms.action.BAZ"

// TODO: Rename parameters
private const val MESSAGE_TEXT = "com.ibashkimi.telegram.sms.extra.PARAM1"
private const val TARGET_PHONE = "com.ibashkimi.telegram.sms.extra.PARAM2"
private const val CHAT_ID = "com.ibashkimi.telegram.sms.extra.PARAM3"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class SendSMSIntentService : IntentService("SendSMSIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_FOO -> {
                val param1 = intent.getStringExtra(MESSAGE_TEXT)
                val param2 = intent.getStringExtra(TARGET_PHONE)
                val param3 = intent.getLongExtra(CHAT_ID, Long.MAX_VALUE)
                handleSendSMS(param1, param2, param3)
            }
//            ACTION_BAZ -> {
//                val param1 = intent.getStringExtra(MESSAGE_TEXT)
//                val param2 = intent.getStringExtra(TARGET_PHONE)
//                handleActionBaz(param1, param2)
//            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleSendSMS(text: String?, number: String?, chatId: Long) {
        try {
            Log.d("SendSMSIntentService", "sending" + text)
            if (number === null || text === null || text.isEmpty()) {
                return
            }
            var chatTitle: String?;
            runBlocking {
                if (chatId == Long.MAX_VALUE) {
                    chatTitle = ""
                }
                val chat = Repository.chats?.getChat(chatId!!)?.first()
                chatTitle = chat?.title
            }
            val sms = SmsManager.getDefault()
            val parts = sms.divideMessage(chatTitle + ":" + text)

            val SENT = "SMS_SENT"
            val DELIVERED = "SMS_DELIVERED"

            val sentPI = PendingIntent.getBroadcast(
                this, 0,
                Intent(SENT), 0
            )

            val deliveredPI = PendingIntent.getBroadcast(
                this, 0,
                Intent(DELIVERED), 0
            )
            val sendList: ArrayList<PendingIntent> = ArrayList()
            sendList.add(sentPI)

            val deliverList: ArrayList<PendingIntent> = ArrayList()
            deliverList.add(deliveredPI)

            sms.sendMultipartTextMessage(number, null, parts, sendList, deliverList)

        } catch (e: Exception) {
            Log.e("SMS sending", e.toString())
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
        TODO("Handle action Baz")
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startSMSSending(context: Context, text: String?, phone: String?, chatId: Long?) {
            val intent = Intent(context, SendSMSIntentService::class.java).apply {
                action = ACTION_FOO
                putExtra(MESSAGE_TEXT, text)
                putExtra(TARGET_PHONE, phone)
                putExtra(CHAT_ID, chatId)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, SendSMSIntentService::class.java).apply {
                action = ACTION_BAZ
                putExtra(MESSAGE_TEXT, param1)
                putExtra(TARGET_PHONE, param2)
            }
            context.startService(intent)
        }
    }
}
