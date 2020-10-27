package com.ibashkimi.telegram

import org.drinkless.td.libcore.telegram.TdApi

interface NewTelegramMessageListener {
    fun onNewMessage(message: TdApi.Message)
}