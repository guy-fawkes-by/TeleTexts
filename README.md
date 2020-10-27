[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

# TeleTexts

A telegram helper app for android created using tdlib library and built with [Jetpack Compose](https://developer.android.com/jetpack/compose).
As a base code for interaction with TdLib I use [https://github.com/indritbashkimi/TelegramExample](https://github.com/indritbashkimi/TelegramExample).  (Forked from it)

To try out this app, you need to: 
* Install the latest **Canary** of Android Studio
* Download the **tdlib** android library from [https://core.telegram.org/tdlib/tdlib.zip](https://core.telegram.org/tdlib/tdlib.zip) and extract the zip file to the root folder of the project
* Obtain application identifier hash for Telegram API access at [https://my.telegram.org](https://my.telegram.org) and store them in the android resources. For example in values/api_keys.xml:
```
<resources>
    <integer name="telegram_api_id">your integer api id</integer>
    <string name="telegram_api_hash">your string api hash</string>
</resources>
```

This app is **work in progress**. Features implemented so far:
- loading chats list 
- listening to updates
- sending new messages from selected chats via SMS
- pause|resume sending updates remotely by sending keywords SMS messages
- set target phone number & activate|pause sending updates from SettingsActivity

**TODO:**
- fix STORAGE permission bug!!!!
- rewrite Telegram sign in to Fragments


**KNOWN BUGS**
- after you `install -> start -> setup -> use -> close app`, on the second start the app would just stop on start screen. When you allow STORAGE permissions to the app (in your phone  settings) & restart the app, it will load & work as expected
- you can't sign out from your telegram account, only delete the app
- if you misprinted your phone or auth code on sign in step, you have to restart the app

APK & russian instruction is stored [here](https://yadi.sk/d/mXerws5pZSem3w?w=1)


## License
    Copyright (c) 2020 Indrit Bashkimi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
