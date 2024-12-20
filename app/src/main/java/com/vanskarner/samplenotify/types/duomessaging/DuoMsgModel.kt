package com.vanskarner.samplenotify.types.duomessaging

internal data class DuoMsgModel(
    val id: Long,
    val text: String,
    //true -> The message was received (incoming)| false: The message was sent (outgoing).
    val isIncoming: Boolean
)
