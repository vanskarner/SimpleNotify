package com.vanskarner.samplenotify.types.groupmessaging

import android.os.Bundle
import androidx.core.app.Person
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanskarner.samplenotify.BaseActivity
import com.vanskarner.simplenotify.NotifyMessaging
import com.vanskarner.samplenotify.databinding.BasicBubbleActivityBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//This is just a very simple example of the bubble content.
internal class GroupBubbleActivity : BaseActivity() {
    companion object {
        val contactPhrases1 = Pair(
            Person.Builder().setName("Dina Balearte").build(), listOf(
                "Your mom...",
                "My cat rum rum...",
                "I'm not here for your tears",
                "Those who commit crimes do so because they do not have an income, surely.",
                "Peruvian families can eat with only 10 soles.",
                "Stop making up false stories or we will have a Pinocchio collection.",
                "I greet my friend, my wayki, the governor of Ayacucho. The Rolex he gave me is nice."
            )
        )
        val contactPhrases2 = Pair(
            Person.Builder().setName("Ministroll").build(), listOf(
                "To go up, anything goes",
                "Keep that idiot from 'La Encerrona' in check.",
                "I do not respond to qualifiers, I respond with my work.",
                "Have you come to interview or rate me?",
                "I don’t want to be an instructor. I want to be President, man!",
                "It's true, there are still deaths, but there are fewer robberies."
            )
        )

        fun groupBubbleMsgSamples(): ArrayList<NotifyMessaging> {
            return arrayListOf(
                NotifyMessaging.ContactMsg(
                    contactPhrases2.second.random(),
                    System.currentTimeMillis() - (3 * 60 * 1000),
                    contactPhrases2.first
                ),
                NotifyMessaging.ContactMsg(
                    contactPhrases1.second.random(),
                    System.currentTimeMillis(),
                    contactPhrases1.first
                )
            )
        }
    }

    private fun NotifyMessaging.toModel(): GroupMsgModel {
        return when (this) {
            is NotifyMessaging.ContactMsg -> {
                val name = person.name.toString()
                val id = name.hashCode().toLong()
                GroupMsgModel(id, name, msg.toString(), true)
            }

            is NotifyMessaging.YourMsg -> GroupMsgModel(55L, "You", msg.toString(), false)
        }
    }

    private val groupMessages: MutableList<GroupMsgModel> by lazy {
        groupBubbleMsgSamples().map { it.toModel() }.toMutableList()
    }
    private val messagesAdapter: GroupMsgAdapter by lazy { GroupMsgAdapter() }
    private lateinit var binding: BasicBubbleActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BasicBubbleActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rcvMessages.adapter = messagesAdapter
        messagesAdapter.submitList(groupMessages)
        binding.send.setOnClickListener {
            val myMsg = binding.input.text.toString()
            if (myMsg.isNotEmpty()) {
                val yourMsg = NotifyMessaging.YourMsg(myMsg, System.currentTimeMillis())
                sendMsg(yourMsg)
                binding.input.text.clear()
                lifecycleScope.launch {
                    delay(2000)
                    val selectedContact = listOf(contactPhrases1, contactPhrases2).random()
                    val contactMsg = NotifyMessaging.ContactMsg(
                        selectedContact.second.random(),
                        System.currentTimeMillis(),
                        selectedContact.first
                    )
                    sendMsg(contactMsg)
                }
            }
        }
    }

    private fun sendMsg(notifyMsg: NotifyMessaging) {
        groupMessages.add(notifyMsg.toModel())
        messagesAdapter.submitList(groupMessages)
        val linearLayoutManager = binding.rcvMessages.layoutManager as LinearLayoutManager
        linearLayoutManager.scrollToPosition(groupMessages.size - 1)
    }

}
