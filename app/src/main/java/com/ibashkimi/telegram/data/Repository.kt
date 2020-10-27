package com.ibashkimi.telegram.data

import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.data.messages.MessagesRepository

object Repository{
    var client: TelegramClient? = null
    var chats: ChatsRepository? = null
    var messages: MessagesRepository? = null
    var users: UserRepository? =null
}