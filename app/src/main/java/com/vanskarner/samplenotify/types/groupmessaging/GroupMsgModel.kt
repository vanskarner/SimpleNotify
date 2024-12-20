package com.vanskarner.samplenotify.types.groupmessaging

internal data class GroupMsgModel(
    val id: Long,
    val name: String,
    val text: String,
    //true -> The message was received (incoming)| false: The message was sent (outgoing).
    val isIncoming: Boolean
)