package com.vanskarner.samplenotify.bubbles

import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vanskarner.samplenotify.R

class MessagesAdapter(private val context: Context) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Messages>() {
        override fun areItemsTheSame(oldItem: Messages, newItem: Messages): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Messages, newItem: Messages): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(newList: List<Messages>) {
        differ.submitList(newList)
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = differ.currentList[position]
        val lp = holder.message.layoutParams as FrameLayout.LayoutParams
        if (message.isIncoming) {
            holder.message.run {
                setBackgroundResource(R.drawable.message_incoming)
                ViewCompat.setBackgroundTintList(this, tint.incoming)
                setPadding(
                    padding.horizontalLong, padding.vertical,
                    padding.horizontalShort, padding.vertical
                )
                layoutParams = lp.apply { gravity = Gravity.START }
            }
        } else {
            holder.message.run {
                setBackgroundResource(R.drawable.message_outgoing)
                ViewCompat.setBackgroundTintList(this, tint.outgoing)
                setPadding(
                    padding.horizontalShort, padding.vertical,
                    padding.horizontalLong, padding.vertical
                )
                layoutParams = lp.apply { gravity = Gravity.END }
            }
        }
        holder.message.text = message.text
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.message)
    }

}