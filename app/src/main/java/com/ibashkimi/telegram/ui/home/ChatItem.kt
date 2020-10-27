package com.ibashkimi.telegram.ui.home

import android.text.format.DateUtils
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.ui.NetworkImage
import kotlinx.coroutines.Dispatchers
import org.drinkless.td.libcore.telegram.TdApi

@Composable
fun ChatTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        maxLines = 1,
        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.W500)
    )
}

@Composable
fun ChatSummary(chat: TdApi.Chat, modifier: Modifier = Modifier) {
//    chat.lastMessage?.content?.let {
//        when (it.constructor) {
//            TdApi.MessageText.CONSTRUCTOR -> BasicChatSummary(
//                text = (it as TdApi.MessageText).text.text,
//                modifier = modifier
//            )
//            TdApi.MessageVideo.CONSTRUCTOR -> HighlightedChatSummary("Video", modifier = modifier)
//            TdApi.MessageCall.CONSTRUCTOR -> HighlightedChatSummary("Call", modifier = modifier)
//            TdApi.MessageAudio.CONSTRUCTOR -> {
//                val message = it as TdApi.MessageAudio
//                Row(modifier = modifier) {
//                    Image(
//                        asset = Icons.Default.Mic,
//                        alignment = Alignment.Center,
//                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
//                    )
//                    Text(
//                        text = message.audio.duration.toTime(),
//                        modifier = Modifier.padding(start = 8.dp)
//                    )
//                }
//            }
//            TdApi.MessageSticker.CONSTRUCTOR -> BasicChatSummary(
//                (it as TdApi.MessageSticker).sticker.emoji + " Sticker",
//                modifier = modifier
//            )
//            TdApi.MessageAnimation.CONSTRUCTOR -> HighlightedChatSummary("GIF", modifier = modifier)
//            TdApi.MessageLocation.CONSTRUCTOR -> HighlightedChatSummary(
//                "Location",
//                modifier = modifier
//            )
//            TdApi.MessageVoiceNote.CONSTRUCTOR -> {
//                val message = it as TdApi.MessageVoiceNote
//                Row(modifier = modifier) {
//                    Image(
//                        asset = Icons.Default.Mic,
//                        alignment = Alignment.Center,
//                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
//                    )
//                    Text(
//                        text = message.voiceNote.duration.toTime(),
//                        modifier = Modifier.padding(start = 8.dp)
//                    )
//                }
//            }
//            TdApi.MessageVideoNote.CONSTRUCTOR -> {
//                val message = it as TdApi.MessageVideoNote
//                Row(modifier = modifier) {
//                    Image(
//                        asset = Icons.Default.Videocam,
//                        alignment = Alignment.Center,
//                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
//                    )
//                    Text(
//                        text = message.videoNote.duration.toTime(),
//                        modifier = Modifier.padding(start = 8.dp)
//                    )
//                }
//            }
//            TdApi.MessageContactRegistered.CONSTRUCTOR -> HighlightedChatSummary(
//                "Joined Telegram!",
//                modifier = modifier
//            )
//            TdApi.MessageChatDeleteMember.CONSTRUCTOR -> HighlightedChatSummary(
//                "${(it as TdApi.MessageChatDeleteMember).userId} left the chat",
//                modifier = modifier
//            )
//            else -> it::class.java.simpleName
//        }
//    }
}

@Composable
fun BasicChatSummary(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.subtitle1,
        maxLines = 2,
        modifier = modifier
    )
}

@Composable
fun HighlightedChatSummary(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.subtitle1,
        color = MaterialTheme.colors.primaryVariant,
        maxLines = 2,
        modifier = modifier
    )
}

@Composable
fun ChatTime(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.caption,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
fun ChatItem(repository: Repository, chat: TdApi.Chat, modifier: Modifier = Modifier) {
    Row(verticalGravity = Alignment.CenterVertically) {
        val imageModifier = Modifier.size(48.dp).clip(shape = CircleShape)

        val chatPhoto =
            repository.chats?.chatImage(chat)
                ?.collectAsState(chat.photo?.small?.local?.path, Dispatchers.IO)
        NetworkImage(
            url = chatPhoto?.value,
            modifier = imageModifier,
            placeHolderRes = R.drawable.ic_person
        )
        val chatIsMonitored = Repository.chats?.isChatMonitored(chat.id)
        Column(modifier = modifier.fillMaxWidth()) {
            Row(verticalGravity = Alignment.CenterVertically) {
                ChatTitle(chat.title, modifier = Modifier.weight(1.0f).background(
                   if (chatIsMonitored == true) Color.LightGray else Color.White
                ))
            }
        }
    }
}

private fun Long.toRelativeTimeSpan(): String =
    DateUtils.getRelativeTimeSpanString(
        this,
        System.currentTimeMillis(),
        DateUtils.SECOND_IN_MILLIS
    ).toString()

private fun Int.toTime(): String {
    val duration = this.toLong()
    val hours: Long = (duration / (60 * 60))
    val minutes = (duration % (60 * 60) / (60))
    val seconds = (duration % (60 * 60) % (60))
    return when {
        minutes == 0L && hours == 0L -> String.format("0:%02d", seconds)
        hours == 0L -> String.format("%02d:%02d", minutes, seconds)
        else -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}

@Composable
fun ClickableChatItem(
    repository: Repository,
    chat: TdApi.Chat,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ChatItem(
        repository,
        chat,
        modifier = modifier.clickable(onClick = onClick).padding(16.dp, 8.dp, 16.dp, 8.dp)
    )
}
