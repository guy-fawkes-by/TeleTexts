package com.ibashkimi.telegram.data.messages

import androidx.compose.foundation.Text

import org.drinkless.td.libcore.telegram.TdApi

fun getTextContent(message: TdApi.Message) : String? {
    when (val content = message.content) {
        is TdApi.MessageText -> return (content as TdApi.MessageText).text.text
        is TdApi.MessageVideo -> return (content as TdApi.MessageVideo).caption.text
        is TdApi.MessagePhoto -> return (content as TdApi.MessagePhoto).caption.text
        else -> return null
    }
}