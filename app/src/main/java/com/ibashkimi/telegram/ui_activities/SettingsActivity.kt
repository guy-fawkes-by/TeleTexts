package com.ibashkimi.telegram.ui_activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.ibashkimi.telegram.R

class SettingsActivity : AppCompatActivity() {
    var toggle: ToggleButton? = null
    var phoneNumberEditText: EditText? = null
    var signOutButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toggle = findViewById<ToggleButton>(R.id.active_toggle_button)
        toggle?.isChecked = isAppActive()
        toggle?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setSharedPreferences(R.string.status, getString(R.string.active))
            } else {
                setSharedPreferences(R.string.status, getString(R.string.idle))
            }
        }

        phoneNumberEditText = findViewById<EditText>(R.id.target_number_edit_text)
        phoneNumberEditText?.setText(getSharedPreferencesValue(R.string.target_phone_number))
        phoneNumberEditText?.onSubmit { onPhoneNumberSubmit() }
    }

    fun EditText.onSubmit(func: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                func()
            }

            true

        }
    }

    private fun onPhoneNumberSubmit() {
        val newPhone = phoneNumberEditText?.text.toString()
        if (newPhone.isNullOrEmpty()) {
            // fuck
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT)
            phoneNumberEditText?.requestFocus()
            return
        }
        val oldPhone = getSharedPreferencesValue(R.string.target_phone_number)
        if (newPhone === oldPhone) {
            return
        }
        setSharedPreferences(R.string.target_phone_number, newPhone)
        val inputManager: InputMethodManager =
            this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.toggleSoftInput(0, 0)
        Toast.makeText(this, "New phone set", Toast.LENGTH_SHORT)
    }

    fun isAppActive() : Boolean {
        return getSharedPreferencesValue(R.string.status) == getString(R.string.active)
    }

    fun getSharedPreferencesValue(key: Int) : String? {
        val prefs: SharedPreferences =
            getSharedPreferences(this.getString(R.string.settings), Context.MODE_PRIVATE)
        return prefs.getString(this.getString(key), null)
    }

    fun setSharedPreferences(key: Int, value: String) {
        val keyString = this.getString(key)

        val sharedPref = this.getSharedPreferences(
            this.getString(R.string.settings),
            Context.MODE_PRIVATE
        ) ?: return
        with(sharedPref.edit()) {
            putString(keyString, value)
            apply()
        }
    }
}