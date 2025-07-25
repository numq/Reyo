package io.github.numq.reyo.notification.queue

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.numq.reyo.notification.NotificationItem
import kotlinx.coroutines.channels.Channel

interface NotificationQueue {
    val notifications: Channel<NotificationItem>

    fun push(message: String, label: ImageVector? = null)

    fun close()
}