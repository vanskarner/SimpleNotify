package com.vanskarner.samplenotify.types.messaging

data class Messages(
    val id: Long,
    val text: String,
    val isIncoming: Boolean
)
