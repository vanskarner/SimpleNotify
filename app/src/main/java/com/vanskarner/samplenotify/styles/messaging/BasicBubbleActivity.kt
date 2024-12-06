package com.vanskarner.samplenotify.styles.messaging

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanskarner.samplenotify.BaseActivity
import com.vanskarner.simplenotify.NotifyMessaging
import com.vanskarner.samplenotify.R

//This is just a very simple example of the bubble content.
class BasicBubbleActivity : BaseActivity() {
    companion object {
        fun bubbleMessageSamples(): ArrayList<NotifyMessaging> {
            return arrayListOf(
                NotifyMessaging.ContactMsg(
                    "The Minister of the Interior represents the political representation of the interior sector, which is the National Police.",
                    System.currentTimeMillis() - (5 * 60 * 1000)
                ),
                NotifyMessaging.ContactMsg(
                    "How are they going to allow the National Police to go out and fight if they think they are going to change ministers?",
                    System.currentTimeMillis() - (4 * 60 * 1000)
                ),
                NotifyMessaging.YourMsg(
                    "The minister announced in national coverage the capture of Ivan Quispe Palomino as \"second in command\" of \"Sendero Luminoso\".",
                    System.currentTimeMillis() - (3 * 60 * 1000)
                ),
                NotifyMessaging.YourMsg(
                    "When in reality, he was a bricklayer and a collaborator of the police. This minister is pure show, that's why they call him \"Ministroll\".",
                    System.currentTimeMillis() - (2 * 60 * 1000)
                )
            )
        }
    }

    private val sampleMessages: MutableList<Messages> by lazy {
        bubbleMessageSamples().mapIndexed { index, notifyMessaging ->
            when (notifyMessaging) {
                is NotifyMessaging.ContactMsg ->
                    Messages(index.toLong(), notifyMessaging.msg.toString(), true)

                is NotifyMessaging.YourMsg ->
                    Messages(index.toLong(), notifyMessaging.msg.toString(), false)
            }
        }.toMutableList()
    }
    private val messagesAdapter: MessagesAdapter by lazy { MessagesAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_bubble_activity)
        val linearLayoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        val rcvMessages: RecyclerView = findViewById(R.id.rcvMessages)
        rcvMessages.adapter = messagesAdapter
        rcvMessages.layoutManager = linearLayoutManager
        messagesAdapter.submitList(sampleMessages)
        setupControl(linearLayoutManager)
    }

    private fun setupControl(linearLayoutManager: LinearLayoutManager) {
        val btnSend: ImageButton = findViewById(R.id.send)
        val edtInput: EditText = findViewById(R.id.input)
        btnSend.setOnClickListener {
            edtInput.text?.let { text ->
                if (text.isNotEmpty()) {
                    val msgId = sampleMessages.last().id + 1
                    sampleMessages.add(Messages(msgId, text.toString(), false))
                    messagesAdapter.submitList(sampleMessages)
                    linearLayoutManager.scrollToPosition(sampleMessages.size - 1)
                    text.clear()
                }
            }
        }
    }

}
