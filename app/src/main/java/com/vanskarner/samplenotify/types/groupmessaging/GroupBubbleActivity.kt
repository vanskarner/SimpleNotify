package com.vanskarner.samplenotify.types.groupmessaging

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
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
        private fun dinaContact(context: Context): Pair<Person, List<String>> {
            val bmp = BitmapFactory.decodeStream(context.assets.open("dina1.jpg"))
            val icon = IconCompat.createWithAdaptiveBitmap(bmp)
            return Pair(
                Person.Builder().setName("Dina Balearte").setIcon(icon).build(), listOf(
                    "Your mom...",
                    "My cat rum rum...",
                    "I'm not here for your tears",
                    "Those who commit crimes do so because they do not have an income, surely.",
                    "Peruvian families can eat with only 10 soles.",
                    "Stop making up false stories or we will have a Pinocchio collection.",
                    "I greet my friend, my wayki, the governor of Ayacucho. The Rolex he gave me is nice."
                )
            )
        }

        private fun santiContact(context: Context): Pair<Person, List<String>> {
            val bmp = BitmapFactory.decodeStream(context.assets.open("ministroll.jpg"))
            val icon = IconCompat.createWithAdaptiveBitmap(bmp)
            return Pair(
                Person.Builder().setName("Ministroll").setIcon(icon).build(), listOf(
                    "To go up, anything goes",
                    "Keep that idiot from 'La Encerrona' in check.",
                    "I do not respond to qualifiers, I respond with my work.",
                    "Have you come to interview or rate me?",
                    "I don’t want to be an instructor. I want to be President, man!",
                    "It's true, there are still deaths, but there are fewer robberies."
                )
            )
        }

        fun groupBubbleMsgSamples(context: Context): ArrayList<NotifyMessaging> {
            val contact1 = dinaContact(context)
            val contact2 = santiContact(context)
            return arrayListOf(
                NotifyMessaging.ContactMsg(
                    contact2.second.random(),
                    System.currentTimeMillis() - (3 * 60 * 1000),
                    contact2.first
                ),
                NotifyMessaging.ContactMsg(
                    contact1.second.random(),
                    System.currentTimeMillis(),
                    contact1.first
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
        groupBubbleMsgSamples(this).map { it.toModel() }.toMutableList()
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
                    val selectedContact = listOf(
                        dinaContact(this@GroupBubbleActivity),
                        santiContact(this@GroupBubbleActivity)
                    ).random()
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
