package com.vanskarner.samplenotify.types.groupmessaging

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
import com.vanskarner.samplenotify.databinding.ItemGroupMsgBinding

internal class GroupMsgAdapter : RecyclerView.Adapter<GroupMsgAdapter.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<GroupMsgModel>() {
        override fun areItemsTheSame(oldItem: GroupMsgModel, newItem: GroupMsgModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupMsgModel, newItem: GroupMsgModel): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(newList: List<GroupMsgModel>) {
        differ.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = ItemGroupMsgBinding.inflate(inflater, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.setupView(item)
    }

    internal class ViewHolder(private val binding: ItemGroupMsgBinding) :
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

        fun setupView(item: GroupMsgModel) {
            binding.name.text = item.name
            binding.msg.text = item.text
            if (item.isIncoming) setupIncomingMsg()
            else setupOutgoingMsg()
        }

        private fun setupIncomingMsg() {
            binding.root.gravity = Gravity.START
            binding.msg.run {
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
            binding.msg.run {
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