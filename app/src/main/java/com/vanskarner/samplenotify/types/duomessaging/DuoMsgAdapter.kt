package com.vanskarner.samplenotify.types.duomessaging

import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vanskarner.samplenotify.R
import com.vanskarner.samplenotify.databinding.ItemDuoMsgBinding

internal class DuoMsgAdapter : RecyclerView.Adapter<DuoMsgAdapter.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<DuoMsgModel>() {
        override fun areItemsTheSame(oldItem: DuoMsgModel, newItem: DuoMsgModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DuoMsgModel, newItem: DuoMsgModel): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(newList: List<DuoMsgModel>) {
        differ.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = ItemDuoMsgBinding.inflate(inflater, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.setupView(item)
    }

    class ViewHolder(private val binding: ItemDuoMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var context: Context = binding.root.context
        private val tint = object {
            val incoming: ColorStateList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.incoming)
            )
            val outgoing: ColorStateList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.outgoing)
            )
        }
        private val padding = object {
            val vertical: Int = context.resources.getDimensionPixelSize(
                R.dimen.message_padding_vertical
            )
            val horizontalShort: Int = context.resources.getDimensionPixelSize(
                R.dimen.message_padding_horizontal_short
            )
            val horizontalLong: Int = context.resources.getDimensionPixelSize(
                R.dimen.message_padding_horizontal_long
            )
        }

        fun setupView(item: DuoMsgModel) {
            binding.message.text = item.text
            if (item.isIncoming) setupIncomingMsg()
            else setupOutgoingMsg()
        }

        private fun setupIncomingMsg() {
            binding.root.gravity = Gravity.START
            binding.message.run {
                setBackgroundResource(R.drawable.message_incoming)
                ViewCompat.setBackgroundTintList(this, tint.incoming)
                setPadding(
                    padding.horizontalLong, padding.vertical,
                    padding.horizontalShort, padding.vertical
                )
            }
        }

        private fun setupOutgoingMsg() {
            binding.root.gravity = Gravity.END
            binding.message.run {
                setBackgroundResource(R.drawable.message_outgoing)
                ViewCompat.setBackgroundTintList(this, tint.outgoing)
                setPadding(
                    padding.horizontalShort, padding.vertical,
                    padding.horizontalLong, padding.vertical
                )
            }
        }

    }

}