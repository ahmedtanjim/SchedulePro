package com.shiftboard.schedulepro.core.messaging

data class MessageContent(
    val notification_id: String,
    val user_id: String?
) {
    companion object {
        fun parseData(data: Map<String, String>): MessageContent? {
            return MessageContent(
                notification_id = data["notification_id"] ?: return null,
                user_id = data["user_id"] ?: return null
            )
        }
    }
}