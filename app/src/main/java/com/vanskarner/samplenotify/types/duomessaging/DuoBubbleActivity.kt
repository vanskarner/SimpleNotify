package com.vanskarner.samplenotify.types.duomessaging

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanskarner.samplenotify.BaseActivity
import com.vanskarner.simplenotify.NotifyMessaging
import com.vanskarner.samplenotify.databinding.BasicBubbleActivityBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//This is just a very simple example of the bubble content.
internal class DuoBubbleActivity : BaseActivity() {
    companion object {
        private fun chooseRandomPhrase(): String {
            val phrases = listOf(
                "El éxito es la suma de pequeños",
                "La vida es como andar en bicicleta",
                "Nunca es demasiado tarde para ser quien",
                "El único límite para nuestros",
                "La creatividad es la inteligencia divirtiéndose.",
                "La mejor forma de predecir el futuro es crearlo."
            )
            return phrases.random()
        }

        fun duoBubbleMsgSamples(): ArrayList<NotifyMessaging> {
            return arrayListOf(
                NotifyMessaging.ContactMsg(chooseRandomPhrase(), System.currentTimeMillis())
            )
        }
    }

    private fun NotifyMessaging.toModel(): DuoMsgModel {
        return when (this) {
            is NotifyMessaging.ContactMsg -> DuoMsgModel(1L, msg.toString(), true)
            is NotifyMessaging.YourMsg -> DuoMsgModel(2L, msg.toString(), false)
        }
    }

    private val duoMessages: MutableList<DuoMsgModel> by lazy {
        duoBubbleMsgSamples().map { it.toModel() }.toMutableList()
    }
    private val duoMsgAdapter: DuoMsgAdapter by lazy { DuoMsgAdapter() }
    private lateinit var binding: BasicBubbleActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BasicBubbleActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rcvMessages.adapter = duoMsgAdapter
        duoMsgAdapter.submitList(duoMessages)
        binding.send.setOnClickListener {
            val myMsg = binding.input.text.toString()
            if (myMsg.isNotEmpty()) {
                val yourMsg = NotifyMessaging.YourMsg(myMsg, System.currentTimeMillis())
                sendMsg(yourMsg)
                binding.input.text.clear()
                lifecycleScope.launch {
                    delay(2000)
                    val contactMsg =
                        NotifyMessaging.ContactMsg(chooseRandomPhrase(), System.currentTimeMillis())
                    sendMsg(contactMsg)
                }
            }
        }
    }

    private fun sendMsg(notifyMsg: NotifyMessaging) {
        duoMessages.add(notifyMsg.toModel())
        duoMsgAdapter.submitList(duoMessages)
        val linearLayoutManager = binding.rcvMessages.layoutManager as LinearLayoutManager
        linearLayoutManager.scrollToPosition(duoMessages.size - 1)
    }

}
