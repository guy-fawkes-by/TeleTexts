package com.ibashkimi.telegram

import android.Manifest.permission.READ_SMS
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.core.content.ContextCompat
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.data.UserRepository
import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.data.messages.MessagesRepository
import com.ibashkimi.telegram.data.messages.getTextContent
import com.ibashkimi.telegram.sms.SendSMSIntentService
import com.ibashkimi.telegram.ui_activities.SettingsActivity
import com.ibashkimi.telegram.ui.MyApp
import org.drinkless.td.libcore.telegram.TdApi
import java.util.*


class MainActivity : AppCompatActivity(), NewTelegramMessageListener, OnAppBarActionClickListener {
    val SMS_PERMISSION_REQUEST_CODE = 101
    val SETTINGS_ACTIVITY_CODE = 12
    val DATE_APP_STARTED = Date()

    private var settingsActivityOpened = false

    private fun getPermissionToReadSMS() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Toast.makeText(
                this,
                getString(R.string.sms_permissions_request_text),
                Toast.LENGTH_LONG
            )
                .show()
            return
        } else if (ContextCompat.checkSelfPermission(this, READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    shouldShowRequestPermissionRationale(READ_SMS)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            ) {
                Toast.makeText(
                    this,
                    "Allow permission to allow the app send & receive SMS",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(READ_SMS), this.SMS_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.size == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Cool! Thank you", Toast.LENGTH_SHORT).show()
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val newClient = TelegramClient(this.application, this)

            Repository.client = newClient
            Repository.chats = ChatsRepository(newClient)
            Repository.messages = MessagesRepository(newClient)
            Repository.users = UserRepository(newClient)
            MyApp(this, Repository)
        }
        getPermissionToReadSMS()
    }

    fun getSetting(key: Int): String? {
        val prefs: SharedPreferences =
            getSharedPreferences(this.getString(R.string.settings), Context.MODE_PRIVATE)
        return prefs.getString(this.getString(key), null)
    }

    override fun onBackPressed() {
        if (!Navigation.pop()) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Repository.client?.close()
    }

    override fun onNewMessage(message: TdApi.Message) {
        if (( message.date.toLong()) * 1000 < DATE_APP_STARTED.time) {
            // * 1000 is because date is in SECONDS, and Date().time is in MILISeconds
            // to skip old unread messages on start
            return
        }
        if (getSetting(R.string.status) != this.getString(R.string.active) || settingsActivityOpened) {
            return
        }
        if (Repository.chats?.isChatMonitored(message.chatId) != true) {
            return
        }
        if (ContextCompat.checkSelfPermission(application, android.Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            getPermissionToReadSMS()
        } else {
            var text = getTextContent(message)
            val number = getSetting(R.string.target_phone_number)
            SendSMSIntentService.startSMSSending(this, text, number, message.chatId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onActionClick(actionId: Int) {
        when (actionId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                settingsActivityOpened = true
                startActivityForResult(intent, SETTINGS_ACTIVITY_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SETTINGS_ACTIVITY_CODE -> this.settingsActivityOpened = false
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}


