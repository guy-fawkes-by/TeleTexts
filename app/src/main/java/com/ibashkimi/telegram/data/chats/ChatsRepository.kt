package com.ibashkimi.telegram.data.chats

import com.ibashkimi.telegram.data.TelegramClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.drinkless.td.libcore.telegram.TdApi

@ExperimentalCoroutinesApi
class ChatsRepository(private val client: TelegramClient) {

    public var chatsToMonitor: ArrayList<Long> = ArrayList()

    private fun getChatIds(): Flow<LongArray> = callbackFlow {
        client.client.send(TdApi.GetChats(TdApi.ChatListMain(), Long.MAX_VALUE, 0, 100)) {
            when (it.constructor) {
                TdApi.Chats.CONSTRUCTOR -> {
                    offer((it as TdApi.Chats).chatIds)
                }
                TdApi.Error.CONSTRUCTOR -> {
                    error("")
                }
                else -> {
                    error("")
                }
            }
            close()
        }
        awaitClose { }
    }

    fun getChats(notMutedOnly: Boolean = false): Flow<List<TdApi.Chat>> = getChatIds()
        .map { ids -> ids.map { getChat(it) } }
        .flatMapLatest { chatsFlow ->
            combine(chatsFlow) { chats ->
                if (notMutedOnly) {
                    chats.toList().filter { chat ->
                        chat.notificationSettings.muteFor == 0
                    }
                } else {
                    chats.toList()
                }
            }
        }

    fun getChat(chatId: Long): Flow<TdApi.Chat> = callbackFlow {
        client.client.send(TdApi.GetChat(chatId)) {
            when (it.constructor) {
                TdApi.Chat.CONSTRUCTOR -> {
                    offer((it as TdApi.Chat))
                }
                TdApi.Error.CONSTRUCTOR -> {
                    error("Something went wrong")
                }
                else -> {
                    error("Something went wrong")
                }
            }
            //close()
        }
        awaitClose { }
    }

    fun isChatMonitored(chatId: Long): Boolean {
        return chatId in chatsToMonitor
    }

    fun chatImage(chat: TdApi.Chat): Flow<String?> =
        chat.photo?.small?.takeIf {
            it.local?.isDownloadingCompleted == false
        }?.id?.let { fileId ->
            client.downloadFile(fileId).map { chat.photo?.small?.local?.path }
        } ?: flowOf(chat.photo?.small?.local?.path)
}